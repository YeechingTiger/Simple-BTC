/** Exercise 2: Verifying Digital Currency Transactions (Simplified Bitcoin)
 *  Course: CIS 6930 Digital Currencies 
 *  Author: Xing He
 *  University: University of Florida
 *  Program Name: ledger  
 */
import java.util.ArrayList;

class TransUnit {
    String trans_ID; 
    ArrayList<InputUnit> arr_of_inseq;
    ArrayList<OutputUnit> arr_of_outseq;

    public TransUnit(String trans_ID, int num_output, int num_input) {
        this.trans_ID = trans_ID;
        this.arr_of_inseq = new ArrayList<>();
        this.arr_of_outseq = new ArrayList<>();
    }

    public void set_input(int seq, String input_trans_ID, int index) {
        this.arr_of_inseq.add(seq, new InputUnit(input_trans_ID, index));
    }

    public void set_output(int seq, String account_name, int value) {
        this.arr_of_outseq.add(seq, new OutputUnit(account_name, value));
    }

    public int get_output_value(int seq) {
        return arr_of_outseq.get(seq).value;
    }
}

class InputUnit {
    String input_trans_ID;
    int index;
    public InputUnit(String input_trans_ID, int index) {
        this.input_trans_ID = input_trans_ID;
        this.index = index;
    }
}

class OutputUnit {
    String account_name;
    int value;
    public OutputUnit(String account_name, int value) {
        this.account_name = account_name;
        this.value = value;
    }
}