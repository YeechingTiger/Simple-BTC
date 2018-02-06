/** Exercise 2: Verifying Digital Currency Transactions (Simplified Bitcoin)
 *  Course: CIS 6930 Digital Currencies 
 *  Author: Xing He
 *  University: University of Florida
 *  Program Name: ledger  
 */

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

class ledger {
    HashMap<String, TransUnit> unit_index;
    LinkedList<TransUnit> unit_list;
    Scanner scanner;
    Util Util;
    boolean interactive_mode = false;
    boolean verbose_mode = false;

    public ledger() {
        this.unit_index = new HashMap<>();
        this.unit_list = new LinkedList<>();
        this.scanner = new Scanner(System.in);
        this.Util = new Util();
    }

    // This function is for wipe
    private void wipe() {
        this.unit_index = new HashMap<>();
        this.unit_list = new LinkedList<>();
        this.Util = new Util();
    }

    // This function is for print
    private void print() {
        for(TransUnit unit: this.unit_list){
            String transaction;
            String input = "";
            String output = unit.arr_of_outseq.size() + "; ";
            transaction = unit.trans_ID + "; " + unit.arr_of_inseq.size() + "; ";
            for (int i = 0; i < unit.arr_of_inseq.size(); i++) {
                input += "(" + unit.arr_of_inseq.get(i).input_trans_ID + ", " + unit.arr_of_inseq.get(i).index + ")";
            }
            input += "; ";

            for (int i = 0; i < unit.arr_of_outseq.size(); i++) {
                output += "(" + unit.arr_of_outseq.get(i).account_name + ", " + Math.abs(unit.arr_of_outseq.get(i).value) + ")";
            }
            transaction += input + output;
            System.out.println(transaction);
        }
    }

    // This function is for toggle_interative_mode
    private void toggle_interactive_mode() {
        this.interactive_mode = !this.interactive_mode;
        if (this.interactive_mode == true) {
            this.help_information();
        }
    }        

    // This function is for command summary
    private void help_information() {
        System.out.println("[F]ile\n[T]ransaction\n[P]rint\n[H]elp\n[D]ump\n[W]ipe\n[I]nteractive\n[V]erbose\n[B]alance\n[E]xit");
    }

    // This function is for finding balance by name
    private void find_balance() {
        System.out.print("Enter User: ");
        String name = this.scanner.nextLine();
        if (this.verbose_mode == true) System.out.println(name);
        int balance = 0;
        for(TransUnit unit : this.unit_index.values()) {    
            for (OutputUnit o_unit : unit.arr_of_outseq) {
                if (o_unit.account_name.equals(name)) {
                    int value = o_unit.value;
                    balance += (value > 0) ? value : 0;
                }
            }
        }
        System.out.println(name + " has " + balance);
    }

    // This function is for transaction
    private void make_transaction(String transaction) {
        String trans_ID, input, output;
        int input_num, output_num;
        String[] arr_of_transaction = transaction.split("; ");
        if (arr_of_transaction.length != 5) {
            System.out.println("Error: Transaction format is wrong");
            return;
        }

        /**
         * Step1: Check transaction
         * Step2: Add transaction
         */

        trans_ID = arr_of_transaction[0];
        // Input Stats
        input_num = Integer.valueOf(arr_of_transaction[1]);
        input = arr_of_transaction[2];

        // Output Stats
        output_num = Integer.valueOf(arr_of_transaction[3]);
        output = arr_of_transaction[4];
        
        // Check SHA-1
        String sha1_result = Util.check_sha1(input_num + "; "+ input + "; " + output_num + "; " + output + "\n");
        if (this.verbose_mode == true) System.out.println(sha1_result);
        if (!sha1_result.equals(trans_ID)) {
            System.out.println("Error: Transaction " + trans_ID + " is not correct, and has been changed to " + sha1_result);
            trans_ID = sha1_result;
        }
        // Step1: Check transaction
        if (input_num == 0) {
            System.out.println(trans_ID + ": " + "good");
        }

        if (input_num != 0 && this.Util.transaction_is_valid(this.unit_index, input, output, trans_ID) == false) {
            return;
        }

        // Step2: Add transaction
        this.unit_list.add(this.Util.add_transaction(trans_ID, this.unit_index, input_num, output_num, input, output));
    }

    // This function is for reading file
    private void read_file() {
        
        if (this.interactive_mode == true) {
            System.out.print("Enter File Name: ");
        }
        
        String filename = this.scanner.nextLine();
        try {
            File file = new File(filename);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                this.make_transaction(line);
            }
            fileReader.close();
            } catch (IOException e) {
                System.out.println("Error: file " + filename +  " cannot be opened for reading");
            }
    }

    // This function is for dumping file
    private void dump() {
        System.out.print("Enter File Name: ");
        String filename = this.scanner.nextLine();
        try { 
            File writename = new File(filename);
            writename.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(writename));  
            for(TransUnit unit: this.unit_list){
                String transaction;
                String input = "";
                String output = unit.arr_of_outseq.size() + "; ";
                transaction = unit.trans_ID + "; " + unit.arr_of_inseq.size() + "; ";
                for (int i = 0; i < unit.arr_of_inseq.size(); i++) {
                    input += "(" + unit.arr_of_inseq.get(i).input_trans_ID + ", " + unit.arr_of_inseq.get(i).index + ")";
                }
                input += "; ";
    
                for (int i = 0; i < unit.arr_of_outseq.size(); i++) {
                    output += "(" + unit.arr_of_outseq.get(i).account_name + ", " + Math.abs(unit.arr_of_outseq.get(i).value) + ")";
                }
                transaction += input + output;
                out.write(transaction + "\r\n"); 
            }
            out.flush();
            out.close();
        } catch (Exception e) {  
             
        }  
    }

    // Programming Entry
    public static void main(String[] args) {
        ledger ledger = new ledger();
        while (true) {

            if (ledger.interactive_mode == true) {
                System.out.print("Select a command: ");
            }

            String command = ledger.scanner.nextLine().toUpperCase();
            switch(command) {
                case "F": ledger.read_file();
                          break;
                case "T": if (ledger.interactive_mode == true) {
                            System.out.print("Enter Transaction: ");
                          }
                          String transaction = ledger.scanner.nextLine();
                          ledger.make_transaction(transaction);
                          break;
                case "P": ledger.print();
                          break;
                case "H": ledger.help_information();
                          break;
                case "D": ledger.dump();
                          break;
                case "W": ledger.wipe();
                          break;
                case "I": ledger.toggle_interactive_mode();
                          break;
                case "V": ledger.verbose_mode = !ledger.verbose_mode;
                          break;
                case "B": ledger.find_balance();
                          break;
                case "E": System.out.println("Good-bye");
                          return;
                default:  break;
            }
        }
    } 
}