package View;

import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import Controller.CarrosControl;
import Controller.CarrosDAO;

import java.awt.AWTException;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;

import Model.Carros;

public class CarrosPainel extends JPanel {
    // Atributos(componentes)
    private JButton cadastrar, apagar, editar;
    private JTextField carMarcaField, carModeloField, carAnoField, carPlacaField, carValorField;
    private List<Carros> carros;
    private JTable table;
    private DefaultTableModel tableModel;
    private int linhaSelecionada = -1;
    private JPanel inputPanel;

    // Construtor(GUI-JPanel)
    public CarrosPainel() {
        super();

        // entrada de dados
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(new JLabel("Cadastro Carros"));

        inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2));

        carMarcaField = addTextFieldInInputPanel("Marca");
        carModeloField = addTextFieldInInputPanel("Modelo");
        carAnoField = addTextFieldInInputPanel("Ano");
        carPlacaField = addTextFieldInInputPanel("Placa");
        carValorField = addTextFieldInInputPanel("Valor");

        add(inputPanel);
        JPanel botoes = new JPanel();

        botoes.add(cadastrar = new JButton("Cadastrar"));
        botoes.add(editar = new JButton("Editar"));
        botoes.add(apagar = new JButton("Apagar"));

        add(botoes);
        // tabela de carros
        JScrollPane jSPane = new JScrollPane();
        add(jSPane);
        tableModel = new DefaultTableModel(new Object[][] {},
                new String[] { "Marca", "Modelo", "Ano", "Placa", "Valor" });
        table = new JTable(tableModel);
        jSPane.setViewportView(table);
        table.setCellSelectionEnabled(false);

        // Cria o banco de dados caso não tenha sido criado
        new CarrosDAO().criaTabela();

        // incluindo elementos do banco na criação do painel
        atualizarTabela();

        // tratamento de Eventos
        this.addEvents();

    }

    private JTextField addTextFieldInInputPanel(String label) {
        inputPanel.add(new JLabel(label));
        JTextField field = new JTextField(20);
        inputPanel.add(field);

        return field;
    }

    private void addEvents() {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                linhaSelecionada = table.rowAtPoint(evt.getPoint());
                if (linhaSelecionada != -1) {
                    carMarcaField.setText((String) table.getValueAt(linhaSelecionada, 0));
                    carModeloField.setText((String) table.getValueAt(linhaSelecionada, 1));
                    carAnoField.setText((String) table.getValueAt(linhaSelecionada, 2));
                    carPlacaField.setText((String) table.getValueAt(linhaSelecionada, 3));
                    carValorField.setText((String) table.getValueAt(linhaSelecionada, 4));
                }
            }
        });

        // Cria um objeto operacoes da classe CarrosControl para executar operações no
        // banco de dados
        CarrosControl operacoes = new CarrosControl(carros, tableModel, table);

        // Configura a ação do botão "cadastrar" para adicionar um novo registro no
        // banco de dados
        cadastrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checarCampoAno() == false) {
                    JOptionPane.showMessageDialog(null, "Ano inválido");
                    return;
                }
                if (checarCampoValor() == false) {
                    JOptionPane.showMessageDialog(null, "Insira um valor válido");
                    return;
                }

                operacoes.cadastrar(carMarcaField.getText(), carModeloField.getText(), carAnoField.getText(),
                        carPlacaField.getText(), carValorField.getText());

                // Limpa os campos de entrada após a operação de cadastro
                carMarcaField.setText("");
                carModeloField.setText("");
                carAnoField.setText("");
                carPlacaField.setText("");
                carValorField.setText("");
            }
        });

        // Configura a ação do botão "editar" para atualizar um registro no banco de
        // dados
        editar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                operacoes.atualizar(carMarcaField.getText(), carModeloField.getText(), carAnoField.getText(),
                        carPlacaField.getText(), carValorField.getText());
                // Limpa os campos de entrada após a operação de atualização
                carMarcaField.setText("");
                carModeloField.setText("");
                carAnoField.setText("");
                carPlacaField.setText("");
                carValorField.setText("");
            }
        });

        // Configura a ação do botão "apagar" para excluir um registro no banco de dados
        apagar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Chama o método "apagar" do objeto operacoes com o valor do campo de entrada
                // "placa"
                operacoes.apagar(carPlacaField.getText());

                // Limpa os campos de entrada após a operação de exclusão
                carMarcaField.setText("");
                carModeloField.setText("");
                carAnoField.setText("");
                carPlacaField.setText("");
                carValorField.setText("");
            }
        });
    }

    private boolean checarCampoAno() {
        try {
            int d = Integer.parseInt(carAnoField.getText());
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private boolean checarCampoValor() {
        try {
            double d = Double.parseDouble (carValorField.getText());
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    // atualizar Tabela de Carros com o Banco de Dados
    private void atualizarTabela() {
        // atualizar tabela pelo banco de dados
        tableModel.setRowCount(0);
        carros = new CarrosDAO().listarTodos();
        for (Carros carro : carros) {
            tableModel.addRow(new Object[] { carro.getMarca(), carro.getModelo(), carro.getAno(), carro.getPlaca(),
                    carro.getValor() });
        }

    }

}
