/** Exercise 2: Verifying Digital Currency Transactions (Simplified Bitcoin)
 *  Course: CIS 6930 Digital Currencies 
 *  Author: Xing He
 *  University: University of Florida
 *  Program Name: ledger  
 */

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
class Util {

    public boolean transaction_is_valid(HashMap<String, TransUnit> unit_index, String input, String output, String trans_ID) {
        int input_value = 0, output_value = 0;
        if (unit_index.containsKey(trans_ID)) {
            System.out.println(trans_ID + ": Sorry, invalid transaction. The transaction ID has been used..");
            System.out.println(trans_ID + ": " + "bad");
            return false;
        }
        for(String pair: input.substring(1, input.length() - 1).split("\\)\\(")) {
            String input_trans_ID;
            int index;
            String[] arr_pair = pair.split(", ");
            //System.out.println(arr_pair[0]);
            //System.out.println(arr_pair[1]);
            input_trans_ID = arr_pair[0];
            index = Integer.valueOf(arr_pair[1]);
            if (!unit_index.containsKey(input_trans_ID)) {
                System.out.println(trans_ID + ": Sorry, invalid transaction. No transaction has been found..");
                System.out.println(trans_ID + ": " + "bad");
                return false;
            }
            input_value += unit_index.get(input_trans_ID).get_output_value(index);
        };

        for(String pair: output.substring(1, output.length() - 1).split("\\)\\(")) {
            output_value += Integer.valueOf(pair.split(", ")[1]);
        }
        //System.out.println(output_value + " " + input_value);
        if (output_value > input_value) {
            System.out.println(trans_ID + ": Sorry, invalid transaction. Not enough money…");
            System.out.println(trans_ID + ": " + "bad");
            return false;
        }
        System.out.println(trans_ID + ": " + "good");
        return true;
    }

    public TransUnit add_transaction(String trans_ID, HashMap<String, TransUnit> unit_index, int input_num, int output_num, String input, String output) {
        TransUnit unit = new TransUnit(trans_ID, output_num, input_num);
        int i = 0, j = 0;
        if (input_num != 0) {
            for(String pair: input.substring(1, input.length() - 1).split("\\)\\(")) {
                String input_trans_ID;
                int index;
                String[] arr_pair = pair.split(", ");
                //System.out.println(arr_pair[0]);
                //System.out.println(arr_pair[1]);
                input_trans_ID = arr_pair[0];
                index = Integer.valueOf(arr_pair[1]);
                unit.set_input(i, input_trans_ID, index);
                i++;

                // Mark transaction as spent by setting to -number
                TransUnit old_unit = unit_index.get(input_trans_ID);
                old_unit.arr_of_outseq.get(index).value = -old_unit.arr_of_outseq.get(index).value;
            }
        }
        
        for(String pair: output.substring(1, output.length() - 1).split("\\)\\(")) {
            String account_name;
            int value = 0;
            String[] arr_pair = pair.split(", ");
            //System.out.println(arr_pair[0]);
            //System.out.println(arr_pair[1]);
            account_name = arr_pair[0];
            value = Integer.valueOf(arr_pair[1]);
            unit.set_output(j, account_name, value);
            j++;
        }
        
        unit_index.put(trans_ID, unit);
        return unit;
    }

    public String check_sha1(String input) {
        try {
            MessageDigest mDigest = MessageDigest.getInstance("SHA1");
            byte[] result = mDigest.digest(input.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < result.length; i++) {
                sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString().substring(0, 8);

        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
       
    }
}