package co.edu.unipiloto.laboratoriociclovidaactividad;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //lista que registra los tiempos de las vueltas
    private List<Long> lapTimes = new ArrayList<>();

    //manejar los estados del cronometro
    //determina si el cronometro esta en ejecucion o detenido
    private boolean running;

    //cuenta los segundos cuando el cronometro esta en ejecucion
    private int seconds = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //restaura el estado de la actividad obteniendo valores del paquete
        if(savedInstanceState != null){
            seconds = savedInstanceState.getInt("seconds");
            running = savedInstanceState.getBoolean("running");
        }

        //cuando se lanza la actividad se invoca el hilo para cronometrar
        runTimer();

        //metodos para cada boton
        Button btnStart = findViewById(R.id.start_button);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                running = true;
            }
        });

        Button btnStop = findViewById(R.id.stop_button);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                running = false;
            }
        });

        Button btnReset = findViewById(R.id.reset_button);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                running = false;
                seconds = 0;
            }
        });

        //boton contador de vueltas
        Button btnLapCounter = findViewById(R.id.lapCounter_button);
        btnLapCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (running) {
                    lapTimes.add((long) seconds);
                    seconds = 0;
                    runTimer();

                    if (lapTimes.size() == 5) {
                        showLapTimes(); // Llama al método cuando se completen cinco vueltas.
                    }
                }
            }
        });
    }

    //guarda el estado de las variables en este metodo de la actividad
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("seconds", seconds);
        savedInstanceState.putBoolean("running", running);
    }


    private void runTimer(){
        TextView timeView = findViewById(R.id.time_view);
        //se declara un handler para manejar el tiempo en el hilo de ejecucion
        Handler handler = new Handler();
        //el hanlder se maneja en el metodo post y se instancia el runnable
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds/3600;
                int minutes = (seconds%3600)/60;
                int secs = seconds%60;
                String time = String.format(Locale.getDefault(),"%d:%02d:%02d",hours,minutes,secs);
                timeView.setText(time);
                if(running){
                    seconds++;
                }
                handler.postDelayed(this,1000);
            }
        });
    }

    //se encarga de construir un texto que contiene los tiempos de vuelta formateados y luego lo muestra en un diálogo
    private void showLapTimes() {
        StringBuilder lapTimesText = new StringBuilder();

        for (int i = 0; i < lapTimes.size(); i++) {
            long lapTime = lapTimes.get(i);
            int lapNumber = i + 1;

            int hours = (int) (lapTime / 3600);
            int minutes = (int) ((lapTime % 3600) / 60);
            int seconds = (int) (lapTime % 60);

            String lapTimeString = String.format(Locale.getDefault(), "Vuelta %d: %02d:%02d:%02d\n", lapNumber, hours, minutes, seconds);
            lapTimesText.append(lapTimeString);
        }

        //se muestran los tiempos de las vueltas
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tiempos de Vuelta");
        builder.setMessage(lapTimesText.toString());
        builder.setPositiveButton("Aceptar", null);
        builder.show();
    }


}