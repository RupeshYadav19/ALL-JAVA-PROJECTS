package com.greexam.ui.student;

import com.greexam.dao.TestDAO;
import com.greexam.dao.SubmissionDAO;
import com.greexam.model.Question;
import com.greexam.model.StudentAnswer;
import com.greexam.model.Test;
import com.greexam.model.Test.TestQuestion;
import com.greexam.service.ExamService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestTakingFrame extends JFrame {

    private int submissionId;
    private int testId;
    private Test test;
    private List<TestQuestion> testQuestions;
    
    private ExamService examService = new ExamService();
    private TestDAO testDAO = new TestDAO();
    private SubmissionDAO submissionDAO = new SubmissionDAO();

    private int currentQuestionIndex = 0;
    private int totalSecondsRemaining;
    private Timer timer;
    
    // Tracking time spent per question
    private Map<Integer, Integer> timeSpentMap = new HashMap<>(); // Q index -> seconds
    private int currentQuestionSeconds = 0;

    // UI Components
    private JLabel lblTimer;
    private JLabel lblQuestionInfo;
    private JPanel questionContainer;
    private JPanel palettePanel;
    private JButton[] paletteButtons;

    // Active Input Component
    private Component activeInputComp;

    public TestTakingFrame(int submissionId, int testId) {
        this.submissionId = submissionId;
        this.testId = testId;
        
        loadTestData();
        totalSecondsRemaining = test.getDurationMinutes() * 60;
        
        for (int i = 0; i < testQuestions.size(); i++) {
            timeSpentMap.put(i, 0); // initialize
        }

        initUI();
        startTimer();
        
        // Handle window close
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(TestTakingFrame.this, 
                    "Are you sure you want to exit? Your answers will be auto-submitted.", 
                    "Warning", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    autoSubmit();
                }
            }
        });
    }

    private void loadTestData() {
        test = testDAO.findById(testId);
        testQuestions = test.getTestQuestions();
    }

    private void initUI() {
        setTitle("Test In Progress - " + test.getTitle());
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel(test.getTitle());
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));

        lblTimer = new JLabel("Time Remaining: " + formatTime(totalSecondsRemaining));
        lblTimer.setForeground(new Color(231, 76, 60)); // Red
        lblTimer.setFont(new Font("SansSerif", Font.BOLD, 20));

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblTimer, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Center Area (Question + Navi options)
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        lblQuestionInfo = new JLabel();
        lblQuestionInfo.setFont(new Font("SansSerif", Font.BOLD, 16));
        centerPanel.add(lblQuestionInfo, BorderLayout.NORTH);

        questionContainer = new JPanel(new BorderLayout());
        centerPanel.add(questionContainer, BorderLayout.CENTER);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnPrev = new JButton("Previous");
        btnPrev.addActionListener(e -> navigate(-1));
        
        JButton btnMarkReview = new JButton("Mark for Review");
        btnMarkReview.addActionListener(e -> markForReview());

        JButton btnNext = new JButton("Save & Next");
        btnNext.setBackground(new Color(41, 128, 185));
        btnNext.setForeground(Color.WHITE);
        btnNext.addActionListener(e -> { saveCurrentAnswer(false); navigate(1); });
        
        JButton btnSkip = new JButton("Skip");
        btnSkip.addActionListener(e -> { saveCurrentAnswer(true); navigate(1); });

        navPanel.add(btnPrev);
        navPanel.add(btnMarkReview);
        navPanel.add(btnSkip);
        navPanel.add(btnNext);
        centerPanel.add(navPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // Right Panel (Question Palette)
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(250, 0));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblPalette = new JLabel("Question Palette:");
        lblPalette.setFont(new Font("SansSerif", Font.BOLD, 14));
        rightPanel.add(lblPalette, BorderLayout.NORTH);

        palettePanel = new JPanel(new GridLayout(0, 4, 5, 5));
        paletteButtons = new JButton[testQuestions.size()];
        for (int i = 0; i < testQuestions.size(); i++) {
            JButton btn = new JButton(String.valueOf(i + 1));
            btn.setBackground(Color.LIGHT_GRAY); // Not Visited
            int idx = i;
            btn.addActionListener(e -> jumpToQuestion(idx));
            paletteButtons[i] = btn;
            palettePanel.add(btn);
        }
        
        rightPanel.add(new JScrollPane(palettePanel), BorderLayout.CENTER);

        JButton btnSubmitTest = new JButton("SUBMIT TEST");
        btnSubmitTest.setBackground(new Color(39, 174, 96));
        btnSubmitTest.setForeground(Color.WHITE);
        btnSubmitTest.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnSubmitTest.addActionListener(e -> manualSubmit());
        rightPanel.add(btnSubmitTest, BorderLayout.SOUTH);

        add(rightPanel, BorderLayout.EAST);

        // Load First Question
        showQuestion(currentQuestionIndex);
    }

    private void startTimer() {
        timer = new Timer(1000, e -> {
            totalSecondsRemaining--;
            currentQuestionSeconds++;
            lblTimer.setText("Time Remaining: " + formatTime(totalSecondsRemaining));

            if (totalSecondsRemaining == 600) { // 10 mins
                 JOptionPane.showMessageDialog(this, "10 minutes remaining!", "Warning", JOptionPane.WARNING_MESSAGE);
            } else if (totalSecondsRemaining == 300) { // 5 mins
                 JOptionPane.showMessageDialog(this, "5 minutes remaining!", "Warning", JOptionPane.WARNING_MESSAGE);
            }

            if (totalSecondsRemaining <= 0) {
                timer.stop();
                autoSubmit();
            }
        });
        timer.start();
    }

    private String formatTime(int totalSecs) {
        int h = totalSecs / 3600;
        int m = (totalSecs % 3600) / 60;
        int s = totalSecs % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    private void showQuestion(int index) {
        if (index < 0 || index >= testQuestions.size()) return;
        currentQuestionIndex = index;
        TestQuestion tq = testQuestions.get(index);
        Question q = tq.getQuestion();

        lblQuestionInfo.setText(String.format("Question %d of %d  [Marks: %d]", (index + 1), testQuestions.size(), tq.getMarks()));
        
        questionContainer.removeAll();
        
        // Render based on Question Type
        JTextArea txtQ = new JTextArea(q.getQuestionText());
        txtQ.setEditable(false);
        txtQ.setLineWrap(true);
        txtQ.setWrapStyleWord(true);
        txtQ.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txtQ.setOpaque(false);
        
        JPanel qPanel = new JPanel(new BorderLayout(10, 10));
        qPanel.add(txtQ, BorderLayout.NORTH);

        JPanel ansPanel = new JPanel();
        ansPanel.setLayout(new BoxLayout(ansPanel, BoxLayout.Y_AXIS));

        // Attempt to load existing answer to pre-fill
        StudentAnswer existingSa = getExistingAnswer(tq.getQuestionId());
        String prevAnswer = existingSa != null ? existingSa.getAnswerText() : "";

        switch (q.getQuestionType()) {
            case MCQ -> {
                ButtonGroup bg = new ButtonGroup();
                for (Question.QuestionOption opt : q.getOptions()) {
                    JRadioButton rb = new JRadioButton(opt.getOptionText());
                    rb.setFont(new Font("SansSerif", Font.PLAIN, 14));
                    bg.add(rb);
                    ansPanel.add(rb);
                    if (prevAnswer != null && prevAnswer.equals(opt.getOptionText())) {
                        rb.setSelected(true);
                    }
                }
                activeInputComp = ansPanel; // Special handling required on save
            }
            case ONE_WORD, SHORT_ANSWER -> {
                JTextField txtAns = new JTextField(30);
                txtAns.setFont(new Font("SansSerif", Font.PLAIN, 14));
                if (prevAnswer != null) txtAns.setText(prevAnswer);
                ansPanel.add(txtAns);
                activeInputComp = txtAns;
            }
            case LONG_ANSWER -> {
                JTextArea txtAreaAns = new JTextArea(10, 40);
                txtAreaAns.setFont(new Font("SansSerif", Font.PLAIN, 14));
                txtAreaAns.setLineWrap(true);
                if (prevAnswer != null) txtAreaAns.setText(prevAnswer);
                JScrollPane scroll = new JScrollPane(txtAreaAns);
                ansPanel.add(scroll);
                activeInputComp = txtAreaAns;
            }
            // Fill blank and match left for detailed implementation later
            default -> {
                JTextField txtDef = new JTextField(30);
                txtDef.setToolTipText("Enter answer (For blanks: ans1|||ans2. For match: left:right|||...)");
                if (prevAnswer != null) txtDef.setText(prevAnswer);
                ansPanel.add(new JLabel("Type answer here:"));
                ansPanel.add(txtDef);
                activeInputComp = txtDef;
            }
        }

        qPanel.add(ansPanel, BorderLayout.CENTER);
        questionContainer.add(qPanel, BorderLayout.CENTER);
        
        questionContainer.revalidate();
        questionContainer.repaint();

        // Update palette selection
        for(JButton b : paletteButtons) b.setBorder(UIManager.getBorder("Button.border"));
        paletteButtons[index].setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
    }

    private StudentAnswer getExistingAnswer(int questionId) {
        // Local cache ideally, but for now fetch from DB if needed or maintain list
        List<StudentAnswer> dbAnswers = submissionDAO.findAnswersBySubmission(submissionId);
        return dbAnswers.stream().filter(a -> a.getQuestionId() == questionId).findFirst().orElse(null);
    }

    private void saveCurrentAnswer(boolean skipped) {
        TestQuestion tq = testQuestions.get(currentQuestionIndex);
        String answerText = "";

        if (!skipped && activeInputComp != null) {
            if (activeInputComp instanceof JTextField) {
                answerText = ((JTextField) activeInputComp).getText().trim();
            } else if (activeInputComp instanceof JTextArea) {
                answerText = ((JTextArea) activeInputComp).getText().trim();
            } else if (activeInputComp instanceof JPanel) {
                // MCQ case
                JPanel p = (JPanel) activeInputComp;
                for (Component c : p.getComponents()) {
                    if (c instanceof JRadioButton) {
                        JRadioButton rb = (JRadioButton) c;
                        if (rb.isSelected()) {
                            answerText = rb.getText();
                            break;
                        }
                    }
                }
            }
        }

        // Add accumulated time
        int totalTimeForThisQuestion = timeSpentMap.getOrDefault(currentQuestionIndex, 0) + currentQuestionSeconds;
        timeSpentMap.put(currentQuestionIndex, totalTimeForThisQuestion);

        // Save to DB
        examService.saveAnswer(submissionId, tq.getQuestionId(), answerText, totalTimeForThisQuestion, skipped);

        // Update palette color
        if (skipped) {
            paletteButtons[currentQuestionIndex].setBackground(new Color(231, 76, 60)); // Red
        } else if (!answerText.isEmpty()) {
            paletteButtons[currentQuestionIndex].setBackground(new Color(46, 204, 113)); // Green
        }
        
        currentQuestionSeconds = 0; // reset for next
    }

    private void navigate(int offset) {
        int nextId = currentQuestionIndex + offset;
        if (nextId >= 0 && nextId < testQuestions.size()) {
            showQuestion(nextId);
        }
    }

    private void jumpToQuestion(int index) {
        saveCurrentAnswer(false);
        showQuestion(index);
    }

    private void markForReview() {
        paletteButtons[currentQuestionIndex].setBackground(new Color(241, 196, 15)); // Yellow
    }

    private void manualSubmit() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to submit the test? You cannot change answers after.", 
            "Confirm Submit", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            saveCurrentAnswer(false); // save the one we are on
            timer.stop();
            examService.submitTest(submissionId, false);
            finishTest();
        }
    }

    private void autoSubmit() {
        if(timer != null) timer.stop();
        saveCurrentAnswer(false);
        JOptionPane.showMessageDialog(this, "Time is up! Test auto-submitted.");
        examService.submitTest(submissionId, true);
        finishTest();
    }

    private void finishTest() {
        this.dispose();
        // Return to student dashboard logic can be instantiated here.
        new StudentDashboard().setVisible(true);
    }
}
