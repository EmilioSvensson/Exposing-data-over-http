package se.itu.systemet.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.net.URL;

import se.itu.systemet.domain.Product;
import se.itu.systemet.rest.ApiAccess;
import se.itu.systemet.rest.ApiAccessFactory;
import se.itu.systemet.rest.Param;
import se.itu.systemet.rest.Query;
import se.itu.systemet.rest.QueryFactory;

/**
 * A class representing the GUI for an application for
 * searching Systembolaget products.
 */
public class SearchGUI {

  //Lägger till en kommentar för att testa github 

  // Instance variables below - mostly Swing components for the UI
  private JFrame frame; // this is the actual window
  private JPanel panel; // a panel is a surface to put other components on
  private JPanel form;
  private JTable table; // A table which looks like a spread sheet, kind of
  // input fields for searching
  private JTextField minAlcoField;
  private JTextField maxAlcoField;
  private JTextField minPriceField;
  private JTextField maxPriceField;
  private JLabel resultLabel;
  private JLabel ackel;
  private JButton clearButton;
  private JCheckBox ageCheckbox;

  private JTextArea textArea;

  private List<Product> products;
  private ApiAccess api; // For talking to the REST API

  private String uri;
  private static final String SERVLET_URL = "http://localhost:8080/search/products/all?";

  public SearchGUI() {
    api = ApiAccessFactory.getApiAccess();
    products = api.fetch(QueryFactory.getQuery());
    init(); // Initiate the components
    show(); // Show the frame
  }

  private void init() {
    frame = new JFrame("Search for Systemets products");
    frame.setLayout(new BorderLayout());
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setBackground(Color.GRAY);
    panel = new JPanel(new GridLayout(1, 0));
    GridLayout formLayout = new GridLayout(3,4);
    form = new JPanel(formLayout);
    formLayout.setVgap(2);
    formLayout.setHgap(2);
    table = new JTable(new ProductTableModel(products));
    table.setPreferredScrollableViewportSize(new Dimension(1600, 1600));
    table.setFillsViewportHeight(true);
    table.setRowHeight(30);
    table.setAutoCreateRowSorter(true);
    table.getColumnModel().getColumn(0).setPreferredWidth(400);
    JScrollPane scrollPane = new JScrollPane(table);
    panel.add(scrollPane);
    panel.setOpaque(true);
    frame.add(panel, BorderLayout.CENTER);

    minPriceField = new JTextField(6);
    maxPriceField = new JTextField(6);
    minAlcoField = new JTextField(3);
    maxAlcoField = new JTextField(3);

    form.add(new JLabel("Minimum alcohol:"));
    form.add(minAlcoField);
    form.add(new JLabel("Maximum alcohol:"));
    form.add(maxAlcoField);
    form.add(new JLabel("Minimum price:"));
    form.add(minPriceField);
    form.add(new JLabel("Maximum price:"));


    resultLabel = new JLabel(SERVLET_URL);
    clearButton = new JButton("Clear");
    ageCheckbox = new JCheckBox("I'm over 20");


    form.add(maxPriceField);
    form.add(clearButton, BorderLayout.SOUTH);
    form.add(new JLabel(""));
    form.add(new JLabel(""));
    form.add(ageCheckbox, BorderLayout.SOUTH);
    frame.add(resultLabel, BorderLayout.NORTH);
    frame.add(form, BorderLayout.SOUTH);


    resultLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  addListeners();

  underAge();



  }

  private void underAge(){
    minAlcoField.setEnabled(false);
    minAlcoField.setText("0");
    maxAlcoField.setEnabled(false);
    maxAlcoField.setText("0");
  }

  private void show() {
    frame.pack();
    frame.setVisible(true);
  }

  private List<JTextField> textFields() {
    List<JTextField> textFields = Arrays.asList(minPriceField, maxPriceField, minAlcoField, maxAlcoField);
    return textFields;
  }

  private void addListeners() {
    for (JTextField textField : textFields()) {
      textField.getDocument()
        .addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
              newFilter();
            }
            public void insertUpdate(DocumentEvent e) {
              newFilter();
            }
            public void removeUpdate(DocumentEvent e) {
              newFilter();
            }
          });
    }

      addLinkListener();

      clearButton.addActionListener(new ActionListener()
      {
        @Override
        public void actionPerformed(ActionEvent e){
          minPriceField.setText("");
          maxPriceField.setText("");
          minAlcoField.setText("");
          maxAlcoField.setText("");
          resultLabel.setText(SERVLET_URL);
        }
      });


      ageCheckbox.addItemListener(new ItemListener()

      {
        @Override
        public void itemStateChanged(ItemEvent f){
          if (f.getStateChange() == ItemEvent.SELECTED) {
                  minAlcoField.setEnabled(true);
                  minAlcoField.setText("");
              } else {
                minAlcoField.setEnabled(false);
                minAlcoField.setText("0");
                resultLabel.setText(SERVLET_URL);
              }
              if (f.getStateChange() == ItemEvent.SELECTED) {
                    maxAlcoField.setEnabled(true);
                    maxAlcoField.setText("");
                  } else {
                    maxAlcoField.setEnabled(false);
                    maxAlcoField.setText("0");
                    resultLabel.setText(SERVLET_URL);
                  }

        }
      });
  }

  private List<Param> params() {
    List<Param> params = new ArrayList<>();
    if (!"".equals(minAlcoField.getText())) {
      params.add(new Param("min_alcohol", minAlcoField.getText()));
    }
    if (!"".equals(maxAlcoField.getText())) {
      params.add(new Param("max_alcohol", maxAlcoField.getText()));
    }
    if (!"".equals(minPriceField.getText())) {
      params.add(new Param("min_price", minPriceField.getText()));
    }
    if (!"".equals(maxPriceField.getText())) {
      params.add(new Param("max_price", maxPriceField.getText()));
    }
    return params;
  }

  private void addLinkListener() {
    resultLabel.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent me) {
          try {
            Desktop.getDesktop().browse(new URL(uri).toURI());
          } catch (Exception e) {
            System.err.println("Error clicking link: " + e.getMessage());
            resultLabel.setText("link error");
          }
        }
      });
  }

  private void newFilter() {
    Query query = QueryFactory.getQuery();
    for (Param param : params() ) {
      query.addParam(param);
      System.out.println("http://localhost:8080/search/products/all?" + query.toQueryString());


      String link = "<html><a href=\"" + "http://localhost:8080/search/products/all?" +
      query.toQueryString() + "\">" + "http://localhost:8080/search/products/all?" + query.toQueryString() + "</a></html>";
      this.uri = SERVLET_URL + query.toQueryString();

      resultLabel.setText(link);
    }
    table.setModel(new ProductTableModel(api.fetch(query)));


  }
}
