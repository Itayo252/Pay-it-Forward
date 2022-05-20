package com.example.pay_it_forward.Activities;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pay_it_forward.R;
import com.example.pay_it_forward.Utils.Friend;
import com.example.pay_it_forward.Utils.Transfer;
import com.example.pay_it_forward.Utils.ServerUtils;
import com.example.pay_it_forward.Utils.TransferUtils;
import com.example.pay_it_forward.Utils.User;
import com.example.pay_it_forward.Utils.Utils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TransferActivity extends AppCompatActivity {
    //logged in user
    User user;
    ArrayAdapter<Transfer> pendingTransfersArrayAdapter;
    private String spinnerValue;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        user = (User) getIntent().getSerializableExtra(Utils.USER); //get user

        ArrayAdapter<Friend> friendArrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, 0, Stream.concat(Stream.of(new Friend("", "")), Utils.getFriendsList(user).stream()).collect(Collectors.toList()));
        this.<Spinner>findViewById(R.id.spinnerTransferFriends).setAdapter(friendArrayAdapter);
        this.<Spinner>findViewById(R.id.spinnerTransferFriends).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerValue = ((Friend) parent.getItemAtPosition(position)).getPhoneNumber();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        this.<Button>findViewById(R.id.btnDoTransfer).setOnClickListener(v -> transfer());
        initListView();
    }

    /**
     * check if the user exists and the amount is valid
     */
    private void transfer() {
        String otherPhoneNumber;
        if (this.<EditText>findViewById(R.id.etTransferPhoneNumber).getText().toString().length() > 0) {
            otherPhoneNumber = this.<EditText>findViewById(R.id.etTransferPhoneNumber).getText().toString();
        } else if (spinnerValue.length() > 0) {
            otherPhoneNumber = spinnerValue;
        } else {
            Utils.RaiseMessage.raiseNoUserWasChosen(this);
            return;
        }
        if (ServerUtils.userWithPhoneNumberNotExists(otherPhoneNumber)) {
            Utils.RaiseMessage.raiseUserWithPhoneNumberNotFound(this);
        } else if (!Utils.isValidNumber(this.<EditText>findViewById(R.id.etTransferAmount).getText().toString())) {
            Utils.RaiseMessage.raiseNotValidAmount(this);

        } else {
            //if the transfer is valid, execute it
            TransferUtils.transfer(user.getPhoneNumber(), otherPhoneNumber, Integer.parseInt(this.<EditText>findViewById(R.id.etTransferAmount).getText().toString()));
            cleanFields();
        }
    }

    private void cleanFields() {
        this.<EditText>findViewById(R.id.etTransferPhoneNumber).setText("");
        this.<EditText>findViewById(R.id.etTransferAmount).setText("");
        this.<Spinner>findViewById(R.id.spinnerTransferFriends).setSelection(0);
    }

    private void initListView() { //TODO change to recycle view, refer to https://velmurugan-murugesan.medium.com/getting-started-with-recyclerview-example-3c7a48498715
        pendingTransfersArrayAdapter = new ArrayAdapter<Transfer>(this, 0, 0, TransferUtils.getPendingTransfers(user)) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.pending_transfer, parent, false);
                ((TextView) view.findViewById(R.id.tvPendingFromTo)).setText("From: " + ServerUtils.getNameByPhone(getItem(position).getFrom()) + " (" + getItem(position).getFrom() + ")");
                ((TextView) view.findViewById(R.id.tvPendingAmount)).setText(String.valueOf(getItem(position).getAmount()));
                view.findViewById(R.id.btnTransactionAccept).setOnClickListener(v -> acceptTransfer(getItem(position).getId()));
                view.findViewById(R.id.btnTransactionDeny).setOnClickListener(v -> denyTransfer(getItem(position).getId()));
                return view;
            }
        };
        ((ListView) findViewById(R.id.lvPendingTransfers)).setAdapter(pendingTransfersArrayAdapter);
    }

    private void updateTransferState(int transferId, boolean isAccepted) {
        ServerUtils.updateRequest(transferId, "update transfer", isAccepted); //notify server
        pendingTransfersArrayAdapter.remove(getTransferByTransferId(transferId)); //remove entry from list
    }

    /**
     * itreate through the list to find the transfer
     * @param transferId the id whose transfer we want to find
     * @return the appropriate transfer
     */
    private Transfer getTransferByTransferId(int transferId) {
        for (int i = 0; i < pendingTransfersArrayAdapter.getCount(); i++) {
            if (pendingTransfersArrayAdapter.getItem(i).getId() == transferId) {
                return pendingTransfersArrayAdapter.getItem(i);
            }
        }
        return null;
    }

    private void denyTransfer(int transferId) {
        updateTransferState(transferId, false);
    }


    private void acceptTransfer(int transferId) {
        updateTransferState(transferId, true);
    }

}