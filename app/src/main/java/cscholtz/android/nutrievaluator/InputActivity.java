package cscholtz.android.nutrievaluator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class InputActivity extends AppCompatActivity {
    private EditText nombreInput;
    private EditText edadInput;
    private EditText pesoInput;
    private EditText tallaInput;
    private EditText cinturaInput;
    private EditText caderaInput;
    private EditText braquialInput;
    private EditText carpoInput;
    private EditText tricipitalInput;
    private EditText bicipitalInput;
    private EditText suprailiacoInput;
    private EditText subescapularInput;
    private RadioGroup sexoGroup;

    private String nombre;
    private String sexo;
    private String edad;
    private String peso;
    private String talla;
    private String cintura;
    private String cadera;
    private String braquial;
    private String carpo;
    private String tricipital;
    private String bicipital;
    private String suprailiaco;
    private String subescapular;
    private Float[] medidas = new Float[2];
    private Integer[] pliegues = new Integer[4];
    private Float[] circunferencias = new Float[4];
    private sqliteOpenHelper helper = new sqliteOpenHelper(this,"BD1",null,1);
    //reciben los string de texto a poner el el pdf, con valores obtenidos del evaluador
    private String imc;
    private String ipt;
    private String pesoideal;
    private String cmb;
    private String amb;
    private String agb;
    private String pt;
    private String cin;
    private String relcincad;
    private String contextura;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        nombreInput = (EditText) findViewById(R.id.nombre);
        sexoGroup = (RadioGroup) findViewById(R.id.sexo);
        edadInput = (EditText) findViewById(R.id.edad);
        pesoInput = (EditText) findViewById(R.id.peso);
        tallaInput = (EditText) findViewById(R.id.talla);

        cinturaInput = (EditText) findViewById(R.id.cintura);
        caderaInput = (EditText) findViewById(R.id.cadera);
        braquialInput = (EditText) findViewById(R.id.braquial);
        carpoInput = (EditText) findViewById(R.id.carpo);

        tricipitalInput = (EditText) findViewById(R.id.tricipital);
        bicipitalInput = (EditText) findViewById(R.id.bicipital);
        suprailiacoInput  = (EditText) findViewById(R.id.suprailiaco);
        subescapularInput  = (EditText) findViewById(R.id.subescapular);

        Button calcularButton = (Button) findViewById(R.id.calcular_button);

        calcularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tomarInput();
                if(!nombre.isEmpty() && !sexo.isEmpty() && !edad.isEmpty() && !peso.isEmpty() && !talla.isEmpty() && !cintura.isEmpty() && !cadera.isEmpty() && !braquial.isEmpty() && !carpo.isEmpty() && !tricipital.isEmpty() && !bicipital.isEmpty() && !suprailiaco.isEmpty() && !subescapular.isEmpty()){
                    new Intent(getBaseContext(), PdfViewerActivity.class);
                    evaluarDatos();
                    createTemplate();
                }else{
                    Toast.makeText(InputActivity.this, "Llene todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public void tomarInput(){
        nombre = nombreInput.getText().toString();
        edad = edadInput.getText().toString();
        peso = pesoInput.getText().toString();
        talla = tallaInput.getText().toString();
        cintura = cinturaInput.getText().toString();
        cadera = caderaInput.getText().toString();
        braquial = braquialInput.getText().toString();
        carpo = carpoInput.getText().toString();
        tricipital = tricipitalInput.getText().toString();
        bicipital = bicipitalInput.getText().toString();
        suprailiaco = suprailiacoInput.getText().toString();
        subescapular = subescapularInput.getText().toString();
        int sexid = sexoGroup.getCheckedRadioButtonId();
        RadioButton sexoInput = (RadioButton) findViewById(sexid);
        sexo = sexoInput.getText().toString();

        medidas[0] = Float.parseFloat(peso);
        medidas[1] = Float.parseFloat(talla);
        circunferencias[0] = Float.parseFloat(cintura);
        circunferencias[1] = Float.parseFloat(cadera);
        circunferencias[2] = Float.parseFloat(braquial);
        circunferencias[3] = Float.parseFloat(carpo);
        pliegues[0] = Integer.valueOf(tricipital);
        pliegues[1] = Integer.valueOf(bicipital);
        pliegues[2] = Integer.valueOf(suprailiaco);
        pliegues[3] = Integer.valueOf(subescapular);
    }

    public void evaluarDatos(){
        Evaluator ev = new Evaluator(nombre, sexo, new Integer(edad),  medidas, pliegues, circunferencias);
        helper.abrir();
        String e = helper.getIdEdad(new Integer(edad));
        imc = "IMC: "+String.format("%.2f",ev.getIMC()) + " kg/mt² "+ev.evaluarIMC();
        ipt = "%IPT: "+String.format("%.2f",ev.getIPT())+"% "+ev.evaluarIPT();
        pesoideal = "PESO IDEAL: "+String.format("%.2f",ev.getPesoIdeal())+" kg";
        Integer[] rCMB = ev.rangoPercentiles(ev.getCMB(),ev.percentiles(helper.percentiles(e,sexo,"CMB")));
        cmb = "CMB: "+mmFloat(ev.getCMB())+percentilRange(rCMB[0],rCMB[1])+ev.evaluarPercentilesCMB(rCMB[0],rCMB[1]);
        Integer[] rAMB = ev.rangoPercentiles(ev.getAMB(),ev.percentiles(helper.percentiles(e,sexo,"AMB")));
        amb = "AMB: "+mmFloat(ev.getAMB())+percentilRange(rAMB[0],rAMB[1])+ev.evaluarPercentiles(rAMB[0],rAMB[1]);
        Integer[] rAGB = ev.rangoPercentiles(ev.getAGB(),ev.percentiles(helper.percentiles(e,sexo,"AGB")));
        agb = "AGB: "+mmFloat(ev.getAGB())+percentilRange(rAGB[0],rAGB[1])+ev.evaluarPercentiles(rAGB[0],rAGB[1]);
        Integer[] rPT = ev.rangoPercentiles(ev.getPT(),ev.percentiles(helper.percentiles(e,sexo,"PT")));
        pt = "PT: "+mmFloat(ev.getPT())+percentilRange(rPT[0],rPT[1])+ev.evaluarPercentiles(rPT[0],rPT[1]);
        cin = "CINTURA: "+String.format("%.2f",ev.getCin())+" cm "+ev.evaluarCintura();
        relcincad = "REL CINT/CAD: "+String.format("%.2f",ev.getRelCinCad())+" "+ev.evaluarRelCinCad();
        contextura = "CONTEXTURA: "+String.format("%.2f",ev.getContextura())+" "+ev.evaluarContextura();
        helper.cerrar();
    }

    public String mmFloat(double f){
        return String.format("%.0f",f)+"mm ";
    }

    public String percentilRange(Integer p1, Integer p2){
        return "(P"+p1+"- P"+p2+") ";
    }

    public void createTemplate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = sdf.format(new Date());
        TemplatePDF templatePDF;
        templatePDF = new TemplatePDF(getApplicationContext());
        templatePDF.openDocument(nombre+"_"+currentDate);
        templatePDF.addMetaData("Evaluacion Nutricional"+nombre,"evaluacion","cs");
        templatePDF.addTitles("Evaluacion Nutricional","Paciente: "+nombre,currentDate);
        templatePDF.addParagraph(imc);
        templatePDF.addParagraph(ipt);
        templatePDF.addParagraph(pesoideal);
        templatePDF.addParagraph(cmb);
        templatePDF.addParagraph(amb);
        templatePDF.addParagraph(agb);
        templatePDF.addParagraph(pt);
        templatePDF.addParagraph(cin);
        templatePDF.addParagraph(relcincad);
        templatePDF.addParagraph(contextura);
        templatePDF.closeDocument();
        templatePDF.viewPDF();
    }

}
