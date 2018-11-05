package com.trianacodes.script.vapeapp.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.media.TimedMetaData;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.trianacodes.script.vapeapp.R;
import com.trianacodes.script.vapeapp.actividades.CuadroDialogo;
import com.trianacodes.script.vapeapp.basedatos.DbHelper;
import com.trianacodes.script.vapeapp.basedatos.OperacionesBasesDeDatos;
import com.trianacodes.script.vapeapp.entidades.Aromas;

import java.io.File;
import java.io.IOException;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;

//Todo: controlar que Porcentaje desde no sea nunca mayor que porcentaje hasta


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AromaNuevoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AromaNuevoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AromaNuevoFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Spinner desplegable;
    private SeekBar sbPorcentajeDesde, sbMinMaceracion, sbPorcentajeHasta, sbMaxMaceracion;
    private TextView ePorcentajeDesde, ePorcentajeHasta, eMinMaceracion, eMaxMaceracion;
    private EditText eNombre, eMarca, eObservaciones;
    private String controlVacio;
    private RatingBar valoracion;
    private Button btnNuevo, btnImagen;
    private ImageView imageAroma;
    OperacionesBasesDeDatos operacionesDatos;
    private DbHelper bd;
    private Aromas aroma = new Aromas();
    private final String CARPETA_RAIZ = "fotosApp/";
    private final String RUTA_IMAGEN = CARPETA_RAIZ + "VapeApp";
    private String ruta;
    private final int COD_FOTO = 20;
    private final int COD_SELECCIONA = 10;
    private final int COD_REQUEST = 100;
    private Bitmap bitmap;
    private String nombreImagen="";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AromaNuevoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AromaNuevoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AromaNuevoFragment newInstance(String param1, String param2) {
        AromaNuevoFragment fragment = new AromaNuevoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_aroma_nuevo, container, false);

        eNombre = vista.findViewById(R.id.etNombre);
        eMarca = vista.findViewById(R.id.etMarca);
        desplegable = vista.findViewById(R.id.spTipo);
        imageAroma = vista.findViewById(R.id.ivAroma);
        sbPorcentajeDesde = vista.findViewById(R.id.sbPorcentajeDesde);
        ePorcentajeDesde = vista.findViewById(R.id.txtPorcentajeDesde);
        sbPorcentajeHasta = vista.findViewById(R.id.sbPorcentajeHasta);
        ePorcentajeHasta = vista.findViewById(R.id.txtPorcentajeHasta);
        sbMinMaceracion = vista.findViewById(R.id.sbMinimo);
        eMinMaceracion = vista.findViewById(R.id.txtMinimo);
        sbMaxMaceracion = vista.findViewById(R.id.sbMaximo);
        eMaxMaceracion = vista.findViewById(R.id.txtMaximo);
        eObservaciones = vista.findViewById(R.id.etObservaciones);
        valoracion = vista.findViewById(R.id.rbValoracion);
        btnImagen = vista.findViewById(R.id.btnImagen);
        btnNuevo = vista.findViewById(R.id.btnNuevo);
        valoracion.setRating(0);

        if(validaPermisos()){
            btnImagen.setEnabled(true);
        }else{
            btnImagen.setEnabled(false);
        }

        // Obtengo una instancia de la base de datos
        operacionesDatos = OperacionesBasesDeDatos.obtenerInstancia(getContext());
        /* Después del new llamo al constructor de la clase. Si este contructor tuviera que recibir
         *  algún parámetro, se tendría que especificar dentro de los paréntesis.*/
        Procesos();
        controlBotones();

        return vista;

    }

    private boolean validaPermisos() {

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }

        if((getContext().checkSelfPermission(CAMERA)==PackageManager.PERMISSION_GRANTED)&&
                (getContext().checkSelfPermission(WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if((shouldShowRequestPermissionRationale(CAMERA)) ||
                (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},COD_REQUEST);
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==COD_REQUEST){
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                btnImagen.setEnabled(true);
            }else{
                solicitarPermisosManual();
            }
        }

    }

    private void solicitarPermisosManual() {
        final CharSequence[] opciones={"si","no"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(getContext());
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("si")){
                    Intent intent=new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("package",getContext().getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(),"Los permisos no fueron aceptados",Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(getContext());
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
            }
        });
        dialogo.show();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void Procesos() {

        try {

            //Llamo a la función que rellena de datos el desplegable
            rellenaDesplegable();
            controlDesplegable();
            /*Llamo a la función que va a controlar si se cambia el valor del SeekBar y si es así,
            modificar el contenido del EditText correspondiente*/
            controlaSbPorcetajeDesde();
            controlaSbPorcetajeHasta();
            controlaSbMinimo();
            controlaSbMaximo();
             /*Llamo a la función que va a controlar si el contenido del EditText ha cambiado y si es
            así, cambio el valor del SeekBar.*/
            /*controlaEdtPorcentaje();
            controlaEdtMinimo();
            controlaEdtMaximo();*/

        } catch (Exception e) {

            /* Guardo den el SharedPreferences los datos necesarios que hay que mostrar en el
            cuadro de diálogo. Parece que como estoy dentro de un Fragment hay que anteponer al
            getSharedpreferences un objeto de tipo Context (en este caso lo he llamado this.getActivity())*/
            SharedPreferences preferencias = this.getActivity().getSharedPreferences("Dialogos",Context.MODE_PRIVATE);
            SharedPreferences.Editor datosEnviados = preferencias.edit();
            datosEnviados.putString("Titulo",getString(R.string.Errores));
            datosEnviados.putString("Mensaje", getString(R.string.mensaje_error) + " \n" +
                    e.getMessage());
            datosEnviados.apply();
            //Creo un objeto de la clase en la que defino el cuadro de diálogo
            CuadroDialogo dialogoPersonalizado = new CuadroDialogo();
            /*Muestro el cuadro de diálogo pasándo como parámetros el manejador de fragmentos y una
             etiqueta que se va a usar para localizar el cuadro de diálogo para hacer tareas con el
             cuadro de diálogo. He tenido que sustituir el getSupportFragmentManager por
             getFragmentManager ya que estoy llamando a un Fragment desde otro Fragment.*/
            dialogoPersonalizado.show(getFragmentManager(), "personalizado");
            // Creo un objeto de tipo Fragment para almacenar en él el cuadro de diálogo
            android.support.v4.app.Fragment fragmento = getFragmentManager().findFragmentByTag("personalizado");

            // Borro el cuadro de diálogo si no se está mostrando
            if (fragmento != null){

                getFragmentManager().beginTransaction().remove(fragmento).commit();

            }
            //Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

    }

    private void rellenaDesplegable() {

        /*He definido dentro de strings.xml un string-array llamado "tipos", con todos los tipos de
         * aromas. Ahora,, en esta línea creo un adaptador de tipo ArrayAdapter para luego rellenar
         * la lista desplegable (Spinner). Para darle formato (tamaño de la letra, color, etc...) a
         la caja de texto he creado un nuevo layout en donde defino todo esto (spinner_personalizado)
         y es el que asigno al ArrayAdapter*/
        ArrayAdapter tipos = ArrayAdapter.createFromResource(getContext(), R.array.tipos,
                R.layout.spinner_personalizado);
        // Asigno el desplegable de la interfaz al objeto de tipo Spinner que he creado (desplegable)
        // desplegable = findViewById(R.id.spTipo);
        /* Establezco el tipo de lista del adaptador. Como quiero qeu el tamaño de letra del
        desplegable sea otro he creado otro layout (dropdown_spinner_personalizado) en el que defino
        las características (tamaño, padding, color, etc.) y es el que asigno en el
        setDropDownViewResource */
        tipos.setDropDownViewResource(R.layout.dropdown_spinner_personalizado);
        // Le asigno al objeto de tipo Spinner el adaptador que he construido
        desplegable.setAdapter(tipos);

    }

    private void controlDesplegable(){

        try {

            desplegable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    if (adapterView.getItemAtPosition(i).toString().equals("Selecciona")){

                        aroma.setTipo("No asignado");

                    } else{

                        aroma.setTipo(adapterView.getItemAtPosition(i).toString());

                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }

            });

        } catch (Exception e) {

            /* Guardo den el SharedPreferences los datos necesarios que hay que mostrar en el
            cuadro de diálogo*/
            SharedPreferences preferencias = this.getActivity().getSharedPreferences("Dialogos",Context.MODE_PRIVATE);
            SharedPreferences.Editor datosEnviados = preferencias.edit();
            datosEnviados.putString("Titulo",getString(R.string.Errores));
            datosEnviados.putString("Mensaje", getString(R.string.mensaje_error) + " \n " +
                    e.getMessage());
            datosEnviados.apply();
            //Creo un objeto de la clase en la que defino el cuadro de diálogo
            CuadroDialogo dialogoPersonalizado = new CuadroDialogo();
            /*Muestro el cuadro de diálogo pasándo como parámetros el manejador de fragmentos y una
             etiqueta que se va a suar para locarlizar el cuadro de diálogo para hacer tareas con el
             cuadro de diálogo.*/
            dialogoPersonalizado.show(getFragmentManager(), "personalizado");
            // Creo un objeto de tipo Fragment para almacenar en él el cuadro de diálogo
            android.support.v4.app.Fragment fragmento = getFragmentManager().findFragmentByTag("personalizado");

            // Borro el cuadro de diálogo si no se está mostrando
            if (fragmento != null){

                getFragmentManager().beginTransaction().remove(fragmento).commit();

            }

            // Toast.makeText(this,"Error: " + e.getMessage(),Toast.LENGTH_LONG).show();

        }


    }

    private void controlaSbPorcetajeDesde(){

        try {

            sbPorcentajeDesde.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar porcentaje, int i, boolean b) {

                /* Uso String.ValueOf para que el número almacenado en porcentaje.getProgress()
                lo tome el EditText con formato texto, ya que los EditText sólo admiten contenido
                de tipo String.*/

                    ePorcentajeDesde.setText(String.valueOf(porcentaje.getProgress()));

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

        } catch (Exception e) {

            /* Guardo den el SharedPreferences los datos necesarios que hay que mostrar en el
            cuadro de diálogo*/
            SharedPreferences preferencias = this.getActivity().getSharedPreferences("Dialogos",Context.MODE_PRIVATE);
            SharedPreferences.Editor datosEnviados = preferencias.edit();
            datosEnviados.putString("Titulo",getString(R.string.Errores));
            datosEnviados.putString("Mensaje",   getString(R.string.mensaje_error) + " \n " +
                    e.getMessage());
            datosEnviados.apply();
            //Creo un objeto de la clase en la que defino el cuadro de diálogo
            CuadroDialogo dialogoPersonalizado = new CuadroDialogo();
            /*Muestro el cuadro de diálogo pasándo como parámetros el manejador de fragmentos y una
             etiqueta que se va a suar para locarlizar el cuadro de diálogo para hacer tareas con el
             cuadro de diálogo.*/
            dialogoPersonalizado.show(getFragmentManager(), "personalizado");
            // Creo un objeto de tipo Fragment para almacenar en él el cuadro de diálogo
            android.support.v4.app.Fragment fragmento = getFragmentManager().findFragmentByTag("personalizado");

            // Borro el cuadro de diálogo si no se está mostrando
            if (fragmento != null){

                getFragmentManager().beginTransaction().remove(fragmento).commit();

            }

            //Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

    }

    private void controlaSbPorcetajeHasta(){

        try {

            sbPorcentajeHasta.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar porcentaje, int i, boolean b) {

                /* Uso String.ValueOf para que el número almacenado en porcentaje.getProgress()
                lo tome el EditText con formato texto, ya que los EditText sólo admiten contenido
                de tipo String.*/

                    ePorcentajeHasta.setText(String.valueOf(porcentaje.getProgress()));

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

        } catch (Exception e) {

            /* Guardo den el SharedPreferences los datos necesarios que hay que mostrar en el
            cuadro de diálogo*/
            SharedPreferences preferencias = this.getActivity().getSharedPreferences("Dialogos",Context.MODE_PRIVATE);
            SharedPreferences.Editor datosEnviados = preferencias.edit();
            datosEnviados.putString("Titulo",getString(R.string.Errores));
            datosEnviados.putString("Mensaje",   getString(R.string.mensaje_error) + " \n " +
                    e.getMessage());
            datosEnviados.apply();
            //Creo un objeto de la clase en la que defino el cuadro de diálogo
            CuadroDialogo dialogoPersonalizado = new CuadroDialogo();
            /*Muestro el cuadro de diálogo pasándo como parámetros el manejador de fragmentos y una
             etiqueta que se va a suar para locarlizar el cuadro de diálogo para hacer tareas con el
             cuadro de diálogo.*/
            dialogoPersonalizado.show(getFragmentManager(), "personalizado");
            // Creo un objeto de tipo Fragment para almacenar en él el cuadro de diálogo
            android.support.v4.app.Fragment fragmento = getFragmentManager().findFragmentByTag("personalizado");

            // Borro el cuadro de diálogo si no se está mostrando
            if (fragmento != null){

                getFragmentManager().beginTransaction().remove(fragmento).commit();

            }

            //Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

    }

    private void controlaSbMinimo(){

        try {

            sbMinMaceracion.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar minimo, int i, boolean b) {

                 /* Uso String.ValueOf para que el número almacenado en porcentaje.getProgress()
                lo tome el EditText con formato texto, ya que los EditText sólo admiten contenido
                de tipo String.*/

                    eMinMaceracion.setText(String.valueOf(minimo.getProgress()));

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }

            });


        } catch (Exception e) {

            /* Guardo den el SharedPreferences los datos necesarios que hay que mostrar en el
            cuadro de diálogo*/
            SharedPreferences preferencias = this.getActivity().getSharedPreferences("Dialogos",Context.MODE_PRIVATE);
            SharedPreferences.Editor datosEnviados = preferencias.edit();
            datosEnviados.putString("Titulo",getString(R.string.Errores));
            datosEnviados.putString("Mensaje",   getString(R.string.mensaje_error)+ " \n " +
                    e.getMessage());
            datosEnviados.apply();
            //Creo un objeto de la clase en la que defino el cuadro de diálogo
            CuadroDialogo dialogoPersonalizado = new CuadroDialogo();
            /*Muestro el cuadro de diálogo pasándo como parámetros el manejador de fragmentos y una
             etiqueta que se va a suar para locarlizar el cuadro de diálogo para hacer tareas con el
             cuadro de diálogo.*/
            dialogoPersonalizado.show(getFragmentManager(), "personalizado");
            // Creo un objeto de tipo Fragment para almacenar en él el cuadro de diálogo
            android.support.v4.app.Fragment fragmento = getFragmentManager().findFragmentByTag("personalizado");

            // Borro el cuadro de diálogo si no se está mostrando
            if (fragmento != null){

                getFragmentManager().beginTransaction().remove(fragmento).commit();

            }

            //Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }


    }

    private void controlaSbMaximo(){

        try {

            sbMaxMaceracion.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar maximo, int i, boolean b) {

                 /* Uso String.ValueOf para que el número almacenado en porcentaje.getProgress()
                lo tome el EditText con formato texto, ya que los EditText sólo admiten contenido
                de tipo String.*/

                    eMaxMaceracion.setText(String.valueOf(maximo.getProgress()));

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }

            });


        } catch (Exception e) {

            /* Guardo den el SharedPreferences los datos necesarios que hay que mostrar en el
            cuadro de diálogo*/
            SharedPreferences preferencias = this.getActivity().getSharedPreferences("Dialogos",Context.MODE_PRIVATE);
            SharedPreferences.Editor datosEnviados = preferencias.edit();
            datosEnviados.putString("Titulo",getString(R.string.Errores));
            datosEnviados.putString("Mensaje",   getString(R.string.mensaje_error)+ " \n " +
                    e.getMessage());
            datosEnviados.apply();
            //Creo un objeto de la clase en la que defino el cuadro de diálogo
            CuadroDialogo dialogoPersonalizado = new CuadroDialogo();
            /*Muestro el cuadro de diálogo pasándo como parámetros el manejador de fragmentos y una
             etiqueta que se va a suar para locarlizar el cuadro de diálogo para hacer tareas con el
             cuadro de diálogo.*/
            dialogoPersonalizado.show(getFragmentManager(), "personalizado");
            // Creo un objeto de tipo Fragment para almacenar en él el cuadro de diálogo
            android.support.v4.app.Fragment fragmento = getFragmentManager().findFragmentByTag("personalizado");

            // Borro el cuadro de diálogo si no se está mostrando
            if (fragmento != null){

                getFragmentManager().beginTransaction().remove(fragmento).commit();

            }

            //Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

    }

    private void controlBotones() {

        btnNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(compruebaCampos() && controlaMaceracion() && controlaPorcentajes()) {

                    estableceValores();

                    try {

                        // Ejecuto la tarea asíncrona de inserción de registro
                        new inserta().execute();
                        // Inicializo los valores al pulsar el botón añadir
                        eNombre.setText("");
                        eMarca.setText("");
                        desplegable.setSelection(0);
                        sbPorcentajeDesde.setProgress(0);
                        sbPorcentajeHasta.setProgress(0);
                        sbMinMaceracion.setProgress(0);
                        sbMaxMaceracion.setProgress(0);
                        eObservaciones.setText("");
                        ruta = "";

                        //Todo: Ver cómo funciona el RateBar y las imagenes para ponerlo a 0

                        imageAroma.setImageResource(R.drawable.img_base);

                        valoracion.setRating(0);
                        //eNombre.setNextFocusForwardId(R.id.etNombre);
                        eNombre.requestFocus(R.id.etNombre);

                    } catch (Exception e) {

                    /* Guardo den el SharedPreferences los datos necesarios que hay que mostrar en el
                    cuadro de diálogo*/
                        SharedPreferences preferencias = getContext().getSharedPreferences("Dialogos", Context.MODE_PRIVATE);
                        SharedPreferences.Editor datosEnviados = preferencias.edit();
                        datosEnviados.putString("Titulo", getString(R.string.Errores));
                        datosEnviados.putString("Mensaje", getString(R.string.mensaje_error) + " \n " +
                                e.getMessage());
                        datosEnviados.apply();
                        //Creo un objeto de la clase en la que defino el cuadro de diálogo
                        CuadroDialogo dialogoPersonalizado = new CuadroDialogo();
                    /*Muestro el cuadro de diálogo pasándo como parámetros el manejador de fragmentos y una
                     etiqueta que se va a suar para locarlizar el cuadro de diálogo para hacer tareas con el
                    cuadro de diálogo.*/
                        dialogoPersonalizado.show(getFragmentManager(), "personalizado");
                        // Creo un objeto de tipo Fragment para almacenar en él el cuadro de diálogo
                        android.support.v4.app.Fragment fragmento = getFragmentManager().findFragmentByTag("personalizado");

                        // Borro el cuadro de diálogo si no se está mostrando
                        if (fragmento != null) {

                            getFragmentManager().beginTransaction().remove(fragmento).commit();

                        }


                        //Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(),Toast.LENGTH_LONG).show();

                    }
                }

            }

        });

        btnImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cargarImagen();

            }
        });

    }

    private void cargarImagen() {

        final CharSequence[] opciones={"Hacer Foto","Cargar Imagen","Cancelar"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(getContext());
        alertOpciones.setTitle("Seleccione una Opción");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Hacer Foto")){

                    tomarFotografia();

                }else{
                    if (opciones[i].equals("Cargar Imagen")){
                        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/");
                        startActivityForResult(intent.createChooser(intent,"Seleccione la Aplicación"),COD_SELECCIONA);
                    }else{
                        dialogInterface.dismiss();
                    }
                }
            }
        });
        alertOpciones.show();

    }

    private void tomarFotografia() {

        File fileImagen=new File(Environment.getExternalStorageDirectory(),RUTA_IMAGEN);
        boolean isCreada=fileImagen.exists();
        if(isCreada==false){

            isCreada=fileImagen.mkdirs();

        }

        if(isCreada==true){

            nombreImagen = eNombre.getText().toString() + ".jpg";
            if (nombreImagen.equals(".jpg")){

                nombreImagen = System.currentTimeMillis()/1000 + ".jpg";

            }

        }

        ruta=Environment.getExternalStorageDirectory()+
                File.separator+RUTA_IMAGEN+File.separator+nombreImagen;
        File imagen=new File(ruta);
        Intent intent=null;
        intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N) {

            String authorities=getContext().getPackageName()+".provider";
            Uri imageUri=FileProvider.getUriForFile(getContext(),authorities,imagen);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        } else {

            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));

        }

        startActivityForResult(intent,COD_FOTO);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK){

            switch (requestCode){
                case COD_SELECCIONA:
                    Uri miPath=data.getData();
                    imageAroma.setImageURI(miPath);
                    ruta = miPath.toString();
                    try{

                        bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),
                                miPath);
                        imageAroma.setImageBitmap(bitmap);

                    } catch (IOException e){

                        e.printStackTrace();

                    }

                    break;

                case COD_FOTO:
                    MediaScannerConnection.scanFile(getContext(), new String[]{ruta}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {

                                }
                            });

                    bitmap= BitmapFactory.decodeFile(ruta);
                    imageAroma.setImageBitmap(bitmap);

                    break;
            }

            bitmap = redimensionarImagen(bitmap, 600, 800);

        }
    }

    private Bitmap redimensionarImagen(Bitmap bitmap, float anchoNuevo, float altoNuevo) {

        int ancho = bitmap.getWidth();
        int alto = bitmap.getHeight();
        if (ancho > anchoNuevo || alto > altoNuevo){

            float escalaAncho = anchoNuevo / ancho;
            float escalaAlto = altoNuevo / alto;
            Matrix matrix = new Matrix();
            matrix.postScale(escalaAncho,escalaAlto);
            return bitmap.createBitmap(bitmap,0,0,ancho,alto,matrix,false);

        } else {

            return bitmap;

        }

    }

    private boolean compruebaCampos() {

        // Controlo que los campos Nombre y Marca no estén vacíos
        //if (eNombre.getText().toString().isEmpty()){
        if (eNombre.getText().toString().equals("")){

            /* Guardo en el SharedPreferences los datos necesarios que hay que mostrar en el
            cuadro de diálogo. En los fragments, hay que usar this.getActivity.getSharedPreferences,
            en vez de escribir sólo getSharedPreferences.*/
            SharedPreferences preferencias = this.getActivity().getSharedPreferences("Dialogos",Context.MODE_PRIVATE);
            SharedPreferences.Editor datosEnviados = preferencias.edit();
            datosEnviados.putString("Titulo",getString(R.string.aviso));
            datosEnviados.putString("Mensaje", getString(R.string.nombre_blanco));
            datosEnviados.apply();
            //Creo un objeto de la clase en la que defino el cuadro de diálogo
            CuadroDialogo dialogoPersonalizado = new CuadroDialogo();
            /*Muestro el cuadro de diálogo pasándo como parámetros el manejador de fragmentos y una
             etiqueta que se va a suar para locarlizar el cuadro de diálogo para hacer tareas con el
             cuadro de diálogo.*/
            dialogoPersonalizado.show(getFragmentManager(), "personalizado");
            // Creo un objeto de tipo Fragment para almacenar en él el cuadro de diálogo
            android.support.v4.app.Fragment fragmento = getFragmentManager().findFragmentByTag("personalizado");

            // Borro el cuadro de diálogo si no se está mostrando
            if (fragmento != null){

                getFragmentManager().beginTransaction().remove(fragmento).commit();

            }


            //Toast.makeText(this,"El nombre del aroma no puede estar en blanco",Toast.LENGTH_LONG).show();
            eNombre.requestFocus(R.id.etNombre);
            return false;

        }

        // Controlo que los campos Nombre y Marca no estén vacíos
        //if (eNombre.getText().toString().isEmpty()){
        if (eMarca.getText().toString().equals("")){

            /* Guardo den el SharedPreferences los datos necesarios que hay que mostrar en el
            cuadro de diálogo*/
            SharedPreferences preferencias = this.getActivity().getSharedPreferences("Dialogos",Context.MODE_PRIVATE);
            SharedPreferences.Editor datosEnviados = preferencias.edit();
            datosEnviados.putString("Titulo",getString(R.string.aviso));
            datosEnviados.putString("Mensaje", getString(R.string.marca_blanco));
            datosEnviados.apply();
            //Creo un objeto de la clase en la que defino el cuadro de diálogo
            CuadroDialogo dialogoPersonalizado = new CuadroDialogo();
            /*Muestro el cuadro de diálogo pasándo como parámetros el manejador de fragmentos y una
             etiqueta que se va a suar para locarlizar el cuadro de diálogo para hacer tareas con el
             cuadro de diálogo.*/
            dialogoPersonalizado.show(getFragmentManager(), "personalizado");
            // Creo un objeto de tipo Fragment para almacenar en él el cuadro de diálogo
            android.support.v4.app.Fragment fragmento = getFragmentManager().findFragmentByTag("personalizado");

            // Borro el cuadro de diálogo si no se está mostrando
            if (fragmento != null){

                getFragmentManager().beginTransaction().remove(fragmento).commit();

            }


            //Toast.makeText(this,"El nombre del aroma no puede estar en blanco",Toast.LENGTH_LONG).show();
            eMarca.requestFocus(R.id.etMarca);
            return false;

        }

        return true;

    }

    private boolean controlaMaceracion(){

        if (sbMinMaceracion.getProgress() > sbMaxMaceracion.getProgress()){

            /* Guardo den el SharedPreferences los datos necesarios que hay que mostrar en el
            cuadro de diálogo*/
            SharedPreferences preferencias = this.getActivity().getSharedPreferences("Dialogos",Context.MODE_PRIVATE);
            SharedPreferences.Editor datosEnviados = preferencias.edit();
            datosEnviados.putString("Titulo",getString(R.string.aviso));
            datosEnviados.putString("Mensaje", getString(R.string.mensaje_maceracion));
            datosEnviados.apply();
            //Creo un objeto de la clase en la que defino el cuadro de diálogo
            CuadroDialogo dialogoPersonalizado = new CuadroDialogo();
            /*Muestro el cuadro de diálogo pasándo como parámetros el manejador de fragmentos y una
             etiqueta que se va a suar para locarlizar el cuadro de diálogo para hacer tareas con el
             cuadro de diálogo.*/
            dialogoPersonalizado.show(getFragmentManager(), "personalizado");
            // Creo un objeto de tipo Fragment para almacenar en él el cuadro de diálogo
            android.support.v4.app.Fragment fragmento = getFragmentManager().findFragmentByTag("personalizado");

            // Borro el cuadro de diálogo si no se está mostrando
            if (fragmento != null){

                getFragmentManager().beginTransaction().remove(fragmento).commit();

            }

            //Toast.makeText(getApplicationContext(),"El valor del Tiempo Máximo de maceración \n " +
            //"ha de ser mayor que el tiempo mínimo de maceración",Toast.LENGTH_LONG).show();
            //sbMinMaceracion.requestFocus();

            return false;

        }

        return true;

    }

    private boolean controlaPorcentajes() {

        if (sbPorcentajeDesde.getProgress() > sbPorcentajeHasta.getProgress()){

                   /* Guardo den el SharedPreferences los datos necesarios que hay que mostrar en el
            cuadro de diálogo*/
            SharedPreferences preferencias = this.getActivity().getSharedPreferences("Dialogos",Context.MODE_PRIVATE);
            SharedPreferences.Editor datosEnviados = preferencias.edit();
            datosEnviados.putString("Titulo",getString(R.string.aviso));
            datosEnviados.putString("Mensaje", getString(R.string.mensaje_porcentaje));
            datosEnviados.apply();
            //Creo un objeto de la clase en la que defino el cuadro de diálogo
            CuadroDialogo dialogoPersonalizado = new CuadroDialogo();
            /*Muestro el cuadro de diálogo pasándo como parámetros el manejador de fragmentos y una
             etiqueta que se va a suar para locarlizar el cuadro de diálogo para hacer tareas con el
             cuadro de diálogo.*/
            dialogoPersonalizado.show(getFragmentManager(), "personalizado");
            // Creo un objeto de tipo Fragment para almacenar en él el cuadro de diálogo
            android.support.v4.app.Fragment fragmento = getFragmentManager().findFragmentByTag("personalizado");

            // Borro el cuadro de diálogo si no se está mostrando
            if (fragmento != null){

                getFragmentManager().beginTransaction().remove(fragmento).commit();

            }

            //Toast.makeText(getApplicationContext(),"El valor del Tiempo Máximo de maceración \n " +
            //"ha de ser mayor que el tiempo mínimo de maceración",Toast.LENGTH_LONG).show();
            //sbMinMaceracion.requestFocus();

            return false;

        }

        return true;

    }

    private void estableceValores() {

        /*String rutaSinEspacio = ruta;
        rutaSinEspacio = String.replace(" ","");*/
        aroma.setNombre(eNombre.getText().toString());
        aroma.setMarca(eMarca.getText().toString());
        // El valor del tipo se asigna en controlDesplegable()
        aroma.setDesdePorcentaje(sbPorcentajeDesde.getProgress());
        aroma.setHastaPorcentaje(sbPorcentajeHasta.getProgress());
        aroma.setTiempoMinimoMaceracion(sbMinMaceracion.getProgress());
        aroma.setTiempoMaximoMaceracion(sbMaxMaceracion.getProgress());
        aroma.setObservaciones(eObservaciones.getText().toString());

        if (ruta == null){

            aroma.setImagen("");

        } else {

            aroma.setImagen(ruta.replaceAll("\\s+", ""));

        }

        aroma.setValoracion( (double) valoracion.getRating());

    }

    // Creo tarea asíncrona de inserción de registro
    private class inserta extends AsyncTask<Void,Void,Void> {

        /*private WeakReference<AromasActivity> activityReference;
        inserta(AromasActivity context) {
            activityReference = new WeakReference<>(context);
        }*/

        @Override
        protected Void doInBackground(Void... voids) {

            try{

                operacionesDatos.getDb().beginTransaction();
                operacionesDatos.insertarAroma(aroma);
                operacionesDatos.getDb().setTransactionSuccessful();

            } finally {

                operacionesDatos.getDb().endTransaction();

            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);
            //AromasActivity activity = activityReference.get();
            //if (activity == null || activity.isFinishing()) return;
            // Toast.makeText(getApplicationContext(),aroma.getNombre() + " de " + aroma.getMarca(),Toast.LENGTH_LONG).show();

        }

    }

}
