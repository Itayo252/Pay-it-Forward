package com.example.pay_it_forward.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pay_it_forward.R;
import com.example.pay_it_forward.Utils.ServerUtils;
import com.example.pay_it_forward.Utils.TransferUtils;

public class TransferActivity extends AppCompatActivity {

    ListView lv;
    ArrayAdapter<PendingTransfer> pendingTransfersArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        initListView();
    }

    private void initListView() {
        lv = findViewById(R.id.lvPendingTransfers);
        pendingTransfersArrayAdapter = new ArrayAdapter<PendingTransfer>(this, 0, 0, TransferUtils.getPendingTransfers()) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.pending_transfer, parent, false);
                ((TextView) view.findViewById(R.id.tvPendingFromTo)).setText("From: " + getItem(position).from);
                ((TextView) view.findViewById(R.id.tvPendingAmount)).setText(getItem(position).amount);
                view.findViewById(R.id.btnTransactionAccept).setOnClickListener(v -> acceptTransfer(getItem(position).id));
                view.findViewById(R.id.btnTransactionDeny).setOnClickListener(v -> denyTransfer(getItem(position).id));
                return view;
            }
        };
    }

    private void updateTransferState(String transferId, boolean isAccepted) {
        ServerUtils.updateServerTransfer(transferId, isAccepted);
        pendingTransfersArrayAdapter.remove(getTransferByTransferId(transferId));
    }

    private PendingTransfer getTransferByTransferId(String transferId) {
        for (int i = 0; i < pendingTransfersArrayAdapter.getCount(); i++){
            if (pendingTransfersArrayAdapter.getItem(i).id.equals(transferId)){
                return pendingTransfersArrayAdapter.getItem(i);
            }
        }
        return null;
    }

    private void denyTransfer(String transferId) {
        updateTransferState(transferId, false);
    }


    private void acceptTransfer(String transferId) {
        updateTransferState(transferId, true);
    }

}