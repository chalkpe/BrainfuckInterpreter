package pe.chalk.brainfuck;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import android.view.*;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2016-01-07
 */
public class MainActivity extends Activity {
    protected EditText command;
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        command = (EditText) findViewById(R.id.command);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
    public void onCommandButtonClick(View v){
        switch(v.getId()){
            case R.id.command_plus:
                insertText("+");
                break;
                
            case R.id.command_minus:
                insertText("-");
                break;
                
            case R.id.command_previous:
                insertText("<");
                break;
                
            case R.id.command_next:
                insertText(">");
                break;
                
            case R.id.command_output:
                insertText(".");
                break;
                
            case R.id.command_input:
                insertText(",");
                break;
                
            case R.id.command_open:
                insertText("[");
                break;
                
            case R.id.command_close:
                insertText("]");
                break;
                
            case R.id.command_delete:
                deleteText();
                break;
                
            case R.id.command_enter:
                insertText("\n");
                break;
        }
    }
    
    protected void insertText(String text){
        if(command == null) return;
        int start = command.getSelectionStart();
        
        if(start < 0) command.getText().append(text);
        else command.getText().insert(start, text);
    }
    
    protected void deleteText(){
        if(command == null || command.length() == 0) return;
        int start = command.getSelectionStart();
        
        if(start < 0) command.getText().delete(command.length() - 2, command.length() - 1);
        else command.getText().delete(start - 1, start);
    }
}
