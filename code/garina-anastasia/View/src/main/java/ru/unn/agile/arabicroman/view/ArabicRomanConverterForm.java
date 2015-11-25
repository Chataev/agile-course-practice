package ru.unn.agile.arabicroman.view;

import ru.unn.agile.arabicroman.viewmodel.ArabicRomanConverterViewModel;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class ArabicRomanConverterForm {
    private JTextField inputNumber;
    private JTextField outputNumber;
    private JButton convertButton;
    private JButton reverseButton;
    private JPanel mainPanel;
    private JLabel inputNumberFormat;
    private JLabel outputNumberFormat;
    private JLabel errorText;
    private ArabicRomanConverterViewModel viewModel;

    private ArabicRomanConverterForm() { }

    private ArabicRomanConverterForm(final ArabicRomanConverterViewModel viewModel) {
        this.viewModel = viewModel;
        bindDataFromViewToViewModel();
        bindDataFromViewModelToView();

        inputNumber.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent e) {
                bindDataFromViewToViewModel();
                bindDataFromViewModelToView();
            }

            @Override
            public void removeUpdate(final DocumentEvent e) {
                bindDataFromViewToViewModel();
                bindDataFromViewModelToView();
            }

            @Override
            public void changedUpdate(final DocumentEvent e) {
                bindDataFromViewToViewModel();
                bindDataFromViewModelToView();
            }
        });

        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                bindDataFromViewToViewModel();
                viewModel.convert();
                bindDataFromViewModelToView();
            }
        });

        reverseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                viewModel.reverseConvertingDirection();
                bindDataFromViewToViewModel();
                bindDataFromViewModelToView();
            }
        });
    }

    private void bindDataFromViewModelToView() {
        convertButton.setEnabled(viewModel.isConvertButtonEnabled());
        outputNumber.setText(viewModel.getOutputNumber());
        errorText.setText(viewModel.getErrorMessage());
        inputNumberFormat.setText(viewModel.getInputNumberFormat());
        outputNumberFormat.setText(viewModel.getOutputNumberFormat());
    }

    private void bindDataFromViewToViewModel() {
        viewModel.setInputNumber(inputNumber.getText());
    }

    public static void main(final String[] args) {
        JFrame frame = new JFrame("ArabicRomanConverterForm");
        frame.setContentPane(new ArabicRomanConverterForm(
                new ArabicRomanConverterViewModel()).mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

