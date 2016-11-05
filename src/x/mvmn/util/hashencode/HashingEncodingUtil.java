package x.mvmn.util.hashencode;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Provider;
import java.security.Provider.Service;
import java.security.Security;
import java.util.Base64;
import java.util.LinkedHashSet;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class HashingEncodingUtil {

	public static final String[] HASHING_ALGORITHMS;

	static {
		LinkedHashSet<String> hashingAlgorithms = new LinkedHashSet<String>();
		hashingAlgorithms.add("MD5");
		hashingAlgorithms.add("SHA-256");
		hashingAlgorithms.add("SHA");
		for (Provider secProv : Security.getProviders()) {
			for (Service secService : secProv.getServices()) {
				if (secService.getType().equals("MessageDigest")) {
					hashingAlgorithms.add(secService.getAlgorithm());
				}
			}
		}
		HASHING_ALGORITHMS = hashingAlgorithms.toArray(new String[hashingAlgorithms.size()]);
	}

	public HashingEncodingUtil() {
		JFrame mainWindow = new JFrame("Base64 + hash");
		mainWindow.setLayout(new GridLayout(2, 1));
		final JTextArea input = new JTextArea("Hashing and Base64 encoding util by Mykola Makhin");
		final JTextArea base64 = new JTextArea();
		base64.setWrapStyleWord(false);
		base64.setLineWrap(true);
		final JTextField inputMd5 = new JTextField();
		final JTextField base64Md5 = new JTextField();
		base64.setEditable(false);
		inputMd5.setEditable(false);
		base64Md5.setEditable(false);
		input.setBorder(BorderFactory.createTitledBorder("Input"));
		base64.setBorder(BorderFactory.createTitledBorder("Base64"));
		JPanel pnl1 = new JPanel(new BorderLayout());
		mainWindow.add(pnl1);
		JPanel pnl2 = new JPanel(new BorderLayout());
		mainWindow.add(pnl2);
		final JComboBox<String> inputAlg = new JComboBox<String>(HASHING_ALGORITHMS);
		final JComboBox<String> base64Alg = new JComboBox<String>(HASHING_ALGORITHMS);

		inputAlg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				inputMd5.setText(calculateHash(input.getText(), inputAlg.getSelectedItem().toString()));
			}
		});
		base64Alg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				base64Md5.setText(calculateHash(base64.getText(), base64Alg.getSelectedItem().toString()));
			}
		});

		pnl1.add(new JScrollPane(input), BorderLayout.CENTER);
		JPanel subPnl1 = new JPanel(new BorderLayout());
		subPnl1.add(inputMd5, BorderLayout.CENTER);
		subPnl1.add(inputAlg, BorderLayout.EAST);
		pnl1.add(subPnl1, BorderLayout.SOUTH);
		JPanel subPnl2 = new JPanel(new BorderLayout());
		subPnl2.add(base64Md5, BorderLayout.CENTER);
		subPnl2.add(base64Alg, BorderLayout.EAST);
		pnl2.add(new JScrollPane(base64), BorderLayout.CENTER);
		pnl2.add(subPnl2, BorderLayout.SOUTH);

		final Runnable doUpdate = new Runnable() {

			public void run() {
				try {
					String inputText = input.getText();
					inputMd5.setText(calculateHash(inputText, inputAlg.getSelectedItem().toString()));
					String base64Text = encodeToBase64(inputText);
					base64.setText(base64Text);
					base64Md5.setText(calculateHash(base64Text, base64Alg.getSelectedItem().toString()));
				} catch (Throwable throwable) {
					throwable.printStackTrace();
				}
			}
		};

		input.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
				doUpdate.run();
			}

			public void keyPressed(KeyEvent e) {
			}
		});
		doUpdate.run();

		mainWindow.pack();
		mainWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainWindow.setVisible(true);
	}

	protected String encodeToBase64(String input) {
		return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
	}

	protected String calculateHash(String input, String algorithm) {
		try {
			MessageDigest md5 = MessageDigest.getInstance(algorithm);
			md5.update(input.getBytes(StandardCharsets.UTF_8));
			return String.format("%032x", new BigInteger(1, md5.digest()));
		} catch (Throwable t) {
			t.printStackTrace();
			return t.getClass().getName() + " " + t.getMessage();
		}
	}

	public static void main(String args[]) {
		new HashingEncodingUtil();
	}
}
