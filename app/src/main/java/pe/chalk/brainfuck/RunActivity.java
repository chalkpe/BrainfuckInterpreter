package pe.chalk.brainfuck;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2016-01-09
 */
public class RunActivity extends Activity {
    public static RunActivity instance;

    public static class OutHandler extends Handler {
        private final TextView out;
        public OutHandler(TextView out){
            this.out = out;
        }

        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if(msg.what == 2016 && out != null && msg.obj instanceof CharSequence) out.append((CharSequence) msg.obj);
            else if(msg.what == 2015){
                RecyclerView memory = (RecyclerView) RunActivity.instance.findViewById(R.id.memory);
                memory.setLayoutManager(new LinearLayoutManager(RunActivity.instance, LinearLayoutManager.VERTICAL, false));
                memory.setAdapter(new DataAdapter(RunActivity.instance.interpreter.data, RunActivity.instance.interpreter.dataPointer));
            }
        }
    }

    public static class DataHolder extends RecyclerView.ViewHolder {
        protected CardView root;
        protected TextView index;
        protected TextView value;

        public DataHolder(View itemView){
            super(itemView);

            root = (CardView) itemView;
            index = (TextView) itemView.findViewById(R.id.cardIndex);
            value = (TextView) itemView.findViewById(R.id.cardValue);
        }
    }

    public static class DataAdapter extends RecyclerView.Adapter<DataHolder> {
        protected List<Integer> data;
        protected int selected;

        protected String indexFormat, valueFormat;

        public DataAdapter(List<Integer> data, int selected){
            this.data = data;
            this.selected = selected;

            int maxIndexLength = 2;
            int maxDecValueLength = 2;
            int maxHexValueLength = 2;

            if(!this.data.isEmpty()){
                maxIndexLength = Math.max(maxIndexLength, Integer.toString(this.data.size() - 1).length());

                int maxValue = 0; for(int value: this.data) if(value > maxValue) maxValue = value;
                maxDecValueLength = Math.max(maxDecValueLength, Integer.toString(maxValue, 10).length());
                maxHexValueLength = Math.max(maxHexValueLength, Integer.toString(maxValue, 16).length());
            }

            this.indexFormat = String.format(RunActivity.instance.getString(R.string.card_index), maxIndexLength);
            this.valueFormat = String.format(RunActivity.instance.getString(R.string.card_value), maxDecValueLength, maxHexValueLength);
        }

        @Override
        public DataHolder onCreateViewHolder(ViewGroup parent, int viewType){
            return new DataHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_data, parent, false));
        }

        @Override
        public void onBindViewHolder(DataHolder holder, int position){
            int value = this.data.get(position);

            holder.index.setText(String.format(this.indexFormat, position));
            holder.value.setText(String.format(this.valueFormat, value, value));
            holder.root.setCardBackgroundColor(position == this.selected ? Color.parseColor("#80808080") : 0);
        }

        @Override
        public int getItemCount(){
            return this.data.size();
        }
    }

    protected OutHandler handler;
    protected BrainfuckInterpreter interpreter;

    protected final OutputStream output = new OutputStream(){
        @Override
        public void write(int oneByte) throws IOException {
            handler.sendMessage(Message.obtain(handler, 2016, String.valueOf((char) oneByte)));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        RunActivity.instance = this;
        if(getActionBar() != null) getActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if(extras.containsKey("command")){
            handler = new OutHandler((TextView) findViewById(R.id.console));
            interpreter = new BrainfuckInterpreter(extras.getString("command"), output){
                @Override
                public void onFinished(){
                    handler.sendMessage(Message.obtain(handler, 2016, Html.fromHtml("<br><i>[Finished]</i>")));
                    handler.sendEmptyMessage(2015);
                }
            };
            interpreter.start();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_run, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                if(interpreter != null) interpreter.interrupt();
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
