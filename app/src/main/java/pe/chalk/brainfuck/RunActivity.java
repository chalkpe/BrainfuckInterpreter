package pe.chalk.brainfuck;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2016-01-09
 */
public class RunActivity extends Activity {
    public static class OutHandler extends Handler {
        private final TextView out;
        public OutHandler(TextView out){
            this.out = out;
        }

        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if(msg.what == 2016 && out != null && msg.obj instanceof CharSequence) out.append((CharSequence) msg.obj);
        }
    }

    protected OutHandler handler;
    protected BrainfuckInterpreter interpreter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        if(getActionBar() != null) getActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if(extras.containsKey("command")){
            handler = new OutHandler((TextView) findViewById(R.id.console));
            interpreter = new BrainfuckInterpreter(extras.getString("command"), new OutputStream(){
                @Override
                public void write(int oneByte) throws IOException {
                    handler.sendMessage(Message.obtain(handler, 2016, String.valueOf((char) oneByte)));
                }
            }){
                @Override
                public void onFinished(){
                    handler.sendMessage(Message.obtain(handler, 2016, Html.fromHtml("<br><i>[Finished]</i>")));
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
