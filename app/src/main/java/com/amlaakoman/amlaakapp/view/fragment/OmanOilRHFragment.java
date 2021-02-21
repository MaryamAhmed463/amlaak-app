package com.amlaakoman.amlaakapp.view.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amlaakoman.amlaakapp.BuildConfig;
import com.amlaakoman.amlaakapp.MyProgressDialog;
import com.amlaakoman.amlaakapp.R;
import com.amlaakoman.amlaakapp.model.Invoice;
import com.amlaakoman.amlaakapp.model.VehicleReference;
import com.amlaakoman.amlaakapp.view.adapter.OmanOilReportHeaderAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.io.IOException;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OmanOilRHFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OmanOilRHFragment extends Fragment implements OmanOilReportHeaderAdapter.InvoiceSelectionAdapter {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = "PDFGenerator";
    final Calendar calendar = Calendar.getInstance();
    ArrayList<String> vehiclList;
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter1;
    String vehicleItem;
    ValueEventListener listener, listener1;
    DatabaseReference databaseReference;
    Context context;
    DatabaseReference dbrefSearch;
    private RecyclerView recyclerView;
    private OmanOilReportHeaderAdapter omanOilReportHeaderAdapter;
    private ArrayList<Invoice> invoicesArrayList = new ArrayList<>();
    private ArrayList<String> arrayListvcode = new ArrayList<>();
    private ArrayList<Double> arrayAmount = new ArrayList<>();
    private ArrayList<Double> arrayVolume = new ArrayList<>();
    private ArrayList<Double> arraykmpertliter = new ArrayList<>();
    private ImageView img_search, img_export;
    private SimpleDateFormat dateFormat;
    private MyProgressDialog myProgressDialog;
    private String dateFrom, dateTo;
    private Double TA = 0.0, TV = 0.0, avg = 0.0, TSpan = 0.0;
    private ImageView img_noData;
    private OnFragmentInteractionListener mListener;

    public OmanOilRHFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OmanOilRHFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OmanOilRHFragment newInstance(String param1, String param2) {
        OmanOilRHFragment fragment = new OmanOilRHFragment();
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
        View view = inflater.inflate(R.layout.fragment_oman_oil_r_h, container, false);

        final Context mContext = getContext();
        myProgressDialog = new MyProgressDialog(mContext);
        myProgressDialog.showDialog();
        myProgressDialog.cancelAble();

        context = getActivity();

        recyclerView = view.findViewById(R.id.rv_omanOilRH);
        img_search = view.findViewById(R.id.img_search);
        img_export = view.findViewById(R.id.img_export);
        img_noData = view.findViewById(R.id.img_noData);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Invoice");

        myRef.orderByChild("vCode").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postdataSnapshot : dataSnapshot.getChildren()) {
                    Invoice invoice = postdataSnapshot.getValue(Invoice.class);
                    if (invoice.getStation().equals("Oman Oil")) {
                        //invoicesArrayList.add(invoice);
                        if (!arrayListvcode.contains(invoice.getvCode())) {
                            arrayListvcode.add(invoice.getvCode());
                        }

                    }


                }

                if (arrayListvcode.size() > 0) {
                    img_noData.setVisibility(View.INVISIBLE);
                    for (int i = 0; i < arrayListvcode.size(); i++) {
                        final int finalI = i;
                        myRef.orderByChild("vCode").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot postdataSnapshot : dataSnapshot.getChildren()) {
                                    Invoice invoice = postdataSnapshot.getValue(Invoice.class);

                                    if (invoice.getvCode().equals(arrayListvcode.get(finalI))) {
                                        if (invoice.getStation().equals("Oman Oil")) {

                                            if (invoice.getConfirmation().equals("Confirm")) {

                                                TA += invoice.getAmount();
                                                TV += invoice.getVolume();
                                                TSpan += invoice.getKm_span();
                                            }
                                        }
                                    }

                                }

                                avg = TSpan / TV;
                                DecimalFormat avgFormat = new DecimalFormat("###.000");
                                double avgff = Double.parseDouble(avgFormat.format(avg));
                                arraykmpertliter.add(avgff);

                                DecimalFormat amtFormat = new DecimalFormat("###.000");
                                double amtff = Double.parseDouble(amtFormat.format(TA));
                                arrayAmount.add(amtff);

                                DecimalFormat qtyFormat = new DecimalFormat("###.000");
                                double qtyff = Double.parseDouble(qtyFormat.format(TV));
                                arrayVolume.add(qtyff);

                                TV = 0.0;
                                TA = 0.0;
                                avg = 0.0;
                                TSpan = 0.0;

                                omanOilReportHeaderAdapter = new OmanOilReportHeaderAdapter(arrayListvcode, arrayAmount, arrayVolume, arraykmpertliter, getActivity(), OmanOilRHFragment.this);
                                recyclerView.setAdapter(omanOilReportHeaderAdapter);

                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                                recyclerView.setLayoutManager(linearLayoutManager);

                                myProgressDialog.dismissDialog();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                } else {
                    arrayListvcode.clear();
                    arrayVolume.clear();
                    arraykmpertliter.clear();
                    arrayAmount.clear();

                    img_noData.setVisibility(View.VISIBLE);

                    omanOilReportHeaderAdapter = new OmanOilReportHeaderAdapter(arrayListvcode, arrayAmount, arrayVolume, arraykmpertliter, getActivity(), OmanOilRHFragment.this);
                    recyclerView.setAdapter(omanOilReportHeaderAdapter);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(linearLayoutManager);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialogs = new Dialog(mContext);
                dialogs.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialogs.setContentView(R.layout.dialog_search_header_report);
                Button searchH = dialogs.findViewById(R.id.btn_searchReportH);
                final EditText et_dateFrom = dialogs.findViewById(R.id.et_dateFrom);
                final EditText et_dateTo = dialogs.findViewById(R.id.et_dateTo);
                Spinner sp_vehicle = dialogs.findViewById(R.id.sp_vehicle);


                vehiclList = new ArrayList<>();
                vehiclList.add(0, "All");
                databaseReference = FirebaseDatabase.getInstance().getReference("Vehicles");
                adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, vehiclList);
                sp_vehicle.setAdapter(adapter);
                readingVehicleCode();
                sp_vehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        vehicleItem = parent.getItemAtPosition(position).toString();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


                final DatePickerDialog.OnDateSetListener date_from = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        String myFormat = "yyyy/MM/dd"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                        et_dateFrom.setText(sdf.format(calendar.getTime()));
                    }

                };
                final DatePickerDialog.OnDateSetListener date_to = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        String myFormat = "yyyy/MM/dd"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                        et_dateTo.setText(sdf.format(calendar.getTime()));

                    }

                };

                et_dateFrom.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        new DatePickerDialog(getActivity(), date_from, calendar
                                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });
                et_dateTo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        new DatePickerDialog(getActivity(), date_to, calendar
                                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });


                searchH.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //invoicesArrayList.clear();

                        arrayListvcode.clear();
                        arrayVolume.clear();
                        arraykmpertliter.clear();
                        arrayAmount.clear();

                        TV = 0.0;
                        TA = 0.0;
                        avg = 0.0;
                        TSpan = 0.0;

                        dateFrom = et_dateFrom.getText().toString();
                        dateTo = et_dateTo.getText().toString();

                        if (vehicleItem.equals("All")) {

                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            dbrefSearch = firebaseDatabase.getReference("Invoice");
                            dbrefSearch.orderByChild("date").startAt(et_dateFrom.getText().toString()).
                                    endAt(et_dateTo.getText().toString()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot post : dataSnapshot.getChildren()) {
                                        Invoice invoice = post.getValue(Invoice.class);
                                        if (invoice.getStation().equals("Oman Oil")) {
                                            //invoicesArrayList.add(invoice);
                                            if (!arrayListvcode.contains(invoice.getvCode())) {
                                                arrayListvcode.add(invoice.getvCode());
                                            }
                                        }
                                    }

                                    if (arrayListvcode.size() > 0) {
                                        img_noData.setVisibility(View.INVISIBLE);
                                        for (int t = 0; t < arrayListvcode.size(); t++) {
                                            dbrefSearch = FirebaseDatabase.getInstance().getReference("Invoice");
                                            final int finalT = t;
                                            dbrefSearch.orderByChild("date").startAt(et_dateFrom.getText().toString()).
                                                    endAt(et_dateTo.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot post : dataSnapshot.getChildren()) {
                                                        Invoice invoice = post.getValue(Invoice.class);

                                                        if (invoice.getvCode().equals(arrayListvcode.get(finalT))) {
                                                            if (invoice.getStation().equals("Oman Oil")) {
                                                                if (invoice.getConfirmation().equals("Confirm")) {
                                                                    TA += invoice.getAmount();
                                                                    TV += invoice.getVolume();
                                                                    TSpan += invoice.getKm_span();
                                                                }
                                                            }
                                                        }

                                                    }

                                                    avg = TSpan / TV;
                                                    DecimalFormat avgFormat = new DecimalFormat("###.000");
                                                    double avgff = Double.parseDouble(avgFormat.format(avg));
                                                    arraykmpertliter.add(avgff);

                                                    DecimalFormat amtFormat = new DecimalFormat("###.000");
                                                    double amtff = Double.parseDouble(amtFormat.format(TA));
                                                    arrayAmount.add(amtff);

                                                    DecimalFormat qtyFormat = new DecimalFormat("###.000");
                                                    double qtyff = Double.parseDouble(qtyFormat.format(TV));
                                                    arrayVolume.add(qtyff);


                                                    TV = 0.0;
                                                    TA = 0.0;
                                                    avg = 0.0;
                                                    TSpan = 0.0;

                                                    omanOilReportHeaderAdapter = new OmanOilReportHeaderAdapter(arrayListvcode, arrayAmount, arrayVolume, arraykmpertliter, getActivity(), OmanOilRHFragment.this);
                                                    recyclerView.setAdapter(omanOilReportHeaderAdapter);

                                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                                                    recyclerView.setLayoutManager(linearLayoutManager);

                                                    myProgressDialog.dismissDialog();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                        }
                                    } else {
                                        arrayListvcode.clear();
                                        arrayVolume.clear();
                                        arraykmpertliter.clear();
                                        arrayAmount.clear();

                                        img_noData.setVisibility(View.VISIBLE);

                                        omanOilReportHeaderAdapter = new OmanOilReportHeaderAdapter(arrayListvcode, arrayAmount, arrayVolume, arraykmpertliter, getActivity(), OmanOilRHFragment.this);
                                        recyclerView.setAdapter(omanOilReportHeaderAdapter);

                                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                                        recyclerView.setLayoutManager(linearLayoutManager);
                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else if (!vehicleItem.equals("All")) {

                            arrayListvcode.clear();
                            arrayVolume.clear();
                            arraykmpertliter.clear();
                            arrayAmount.clear();

                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            dbrefSearch = firebaseDatabase.getReference("Invoice");
                            dbrefSearch.orderByChild("date").startAt(et_dateFrom.getText().toString()).
                                    endAt(et_dateTo.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot post : dataSnapshot.getChildren()) {
                                        Invoice invoice = post.getValue(Invoice.class);
                                        if (invoice.getStation().equals("Oman Oil")) {
                                            if (invoice.getvCode().equals(vehicleItem)) {
                                                //invoicesArrayList.add(invoice);
                                                if (!arrayListvcode.contains(invoice.getvCode())) {
                                                    arrayListvcode.add(invoice.getvCode());
                                                }
                                            }
                                        }
                                    }

                                    if (arrayListvcode.size() > 0) {
                                        for (int t = 0; t < arrayListvcode.size(); t++) {
                                            dbrefSearch = FirebaseDatabase.getInstance().getReference("Invoice");
                                            final int finalT = t;
                                            dbrefSearch.orderByChild("date").startAt(et_dateFrom.getText().toString()).
                                                    endAt(et_dateTo.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot post : dataSnapshot.getChildren()) {
                                                        Invoice invoice = post.getValue(Invoice.class);

                                                        if (invoice.getvCode().equals(arrayListvcode.get(finalT))) {
                                                            if (invoice.getStation().equals("Oman Oil")) {
                                                                if (invoice.getConfirmation().equals("Confirm")) {
                                                                    TA += invoice.getAmount();
                                                                    TV += invoice.getVolume();
                                                                    TSpan += invoice.getKm_span();

                                                                }
                                                            }
                                                        }

                                                    }
                                                    avg = TSpan / TV;
                                                    DecimalFormat avgFormat = new DecimalFormat("###.000");
                                                    double avgff = Double.parseDouble(avgFormat.format(avg));
                                                    arraykmpertliter.add(avgff);

                                                    DecimalFormat amtFormat = new DecimalFormat("###.000");
                                                    double amtff = Double.valueOf(amtFormat.format(TA));
                                                    arrayAmount.add(amtff);

                                                    DecimalFormat qtyFormat = new DecimalFormat("###.000");
                                                    double qtyff = Double.valueOf(qtyFormat.format(TV));
                                                    arrayVolume.add(qtyff);


                                                    TV = 0.0;
                                                    TA = 0.0;
                                                    avg = 0.0;
                                                    TSpan = 0.0;

                                                    omanOilReportHeaderAdapter = new OmanOilReportHeaderAdapter(arrayListvcode, arrayAmount, arrayVolume, arraykmpertliter, getActivity(), OmanOilRHFragment.this);
                                                    recyclerView.setAdapter(omanOilReportHeaderAdapter);

                                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                                                    recyclerView.setLayoutManager(linearLayoutManager);

                                                    myProgressDialog.dismissDialog();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                        }
                                    } else {
                                        arrayListvcode.clear();
                                        arrayVolume.clear();
                                        arraykmpertliter.clear();
                                        arrayAmount.clear();

                                        img_noData.setVisibility(View.VISIBLE);

                                        omanOilReportHeaderAdapter = new OmanOilReportHeaderAdapter(arrayListvcode, arrayAmount, arrayVolume, arraykmpertliter, getActivity(), OmanOilRHFragment.this);
                                        recyclerView.setAdapter(omanOilReportHeaderAdapter);

                                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                                        recyclerView.setLayoutManager(linearLayoutManager);
                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            /////////////////////////////////
                        }

                        dialogs.dismiss();

                    }
                });


                dialogs.show();
            }
        });

        img_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
                } else {
                    try {
                        generatedPdfForSavedRecords(context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }
        });

        return view;
    }


    private void readingVehicleCode() {

        listener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    VehicleReference vr = item.getValue(VehicleReference.class);
                    vehiclList.add(vr.getVcode());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void generatedPdfForSavedRecords(Context mContext) throws Exception {

        String pdfFileName = "report.pdf";
        String dirPath = Objects.requireNonNull(mContext.getExternalFilesDir("PDF")).getAbsolutePath();

        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File filePath = new File(dir, pdfFileName);

        Log.d(TAG, "generatedPdfForSavedRecords: filePath:" + filePath);

        if (filePath.exists()) {
            filePath.delete();
        }
        FileOutputStream fOut;

        try {
            fOut = new FileOutputStream(filePath);
            PdfWriter writer = new PdfWriter(fOut);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document layoutDocument = new Document(pdfDocument);


            PdfFont rowFontBold = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
            PdfFont rowFont = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
            float rowFontSize = 8.0f;

            //Header
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo_header);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bitmapdata = stream.toByteArray();

            ImageData imageData1 = ImageDataFactory.create(bitmapdata);
            Image image = new Image(imageData1);
            layoutDocument.add(image.setHeight(35).setWidth(530));

            //Document Date
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            String formattedDate = df.format(c);
            Paragraph docDate = new Paragraph("\nDate: " + formattedDate + "\n\n");
            docDate.setFont(rowFont);
            Color dateColor = new DeviceRgb(255, 0, 0);
            docDate.setFontColor(dateColor);
            layoutDocument.add(docDate.setBold().setUnderline().setTextAlignment(TextAlignment.LEFT));


            //Document Name
            Paragraph docTitle = new Paragraph("Oman Oil Invoices Header Report From " + dateFrom + " To " + dateTo + "\n\n");
            docTitle.setFont(rowFont);
            docTitle.setFontSize(16);
            Color myColor = new DeviceRgb(0, 102, 204);
            docTitle.setFontColor(myColor);
            layoutDocument.add(docTitle.setBold().setTextAlignment(TextAlignment.CENTER));

            Table table1 = new Table(new float[]{4, 4, 4, 4}).useAllAvailableWidth().setFixedLayout();

            //Table Header
            Color cellbkg = new DeviceRgb(143, 33, 102);
            Color fontWhite = new DeviceRgb(255, 255, 255);
            table1.addCell(new Paragraph("Vehicle Code").setFont(rowFontBold).setTextAlignment(TextAlignment.CENTER).setFontSize(rowFontSize).setBackgroundColor(cellbkg).setFontColor(fontWhite).setBorder(Border.NO_BORDER));
            table1.addCell(new Paragraph("Total Quantity(Litre)").setFont(rowFontBold).setTextAlignment(TextAlignment.CENTER).setFontSize(rowFontSize).setBackgroundColor(cellbkg).setFontColor(fontWhite).setBorder(Border.NO_BORDER));
            table1.addCell(new Paragraph("Total Amount(R.O)").setFont(rowFontBold).setTextAlignment(TextAlignment.CENTER).setFontSize(rowFontSize).setBackgroundColor(cellbkg).setFontColor(fontWhite).setBorder(Border.NO_BORDER));
            table1.addCell(new Paragraph("Average KM/Litre").setFont(rowFontBold).setTextAlignment(TextAlignment.CENTER).setFontSize(rowFontSize).setBackgroundColor(cellbkg).setFontColor(fontWhite).setBorder(Border.NO_BORDER));


            //Table Records

            for (int i = 0; i < arrayListvcode.size(); i++) {
                table1.addCell(new Paragraph(String.valueOf(arrayListvcode.get(i))).setFont(rowFont).setTextAlignment(TextAlignment.LEFT).setFontSize(rowFontSize));
                table1.addCell(new Paragraph(String.valueOf(arrayVolume.get(i))).setFont(rowFont).setTextAlignment(TextAlignment.LEFT).setFontSize(rowFontSize));
                table1.addCell(new Paragraph(String.valueOf(arrayAmount.get(i))).setFont(rowFont).setTextAlignment(TextAlignment.LEFT).setFontSize(rowFontSize));
                table1.addCell(new Paragraph(String.valueOf(arraykmpertliter.get(i))).setFont(rowFont).setTextAlignment(TextAlignment.LEFT).setFontSize(rowFontSize));

            }


            layoutDocument.add(table1);

            //footer
            Bitmap bitmapf = BitmapFactory.decodeResource(getResources(), R.drawable.footer);
            ByteArrayOutputStream streamf = new ByteArrayOutputStream();
            bitmapf.compress(Bitmap.CompressFormat.JPEG, 100, streamf);
            byte[] bitmapdataf = streamf.toByteArray();

            ImageData imageDataf = ImageDataFactory.create(bitmapdataf);
            Image imagef = new Image(imageDataf);

            layoutDocument.add(imagef.setHeight(35).setWidth(530).setFixedPosition(30, 10));
            layoutDocument.close();


            layoutDocument.close();

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Doc Creation Failed :" + e.getLocalizedMessage());
            throw e;
            //Toast.makeText(mContext,"Your Mobile May not support This feature", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            throw e;
        } catch (Error e) {
            //Toast.makeText(mContext,"Your Mobile May not support all the language Characters", Toast.LENGTH_LONG).show();

        }
        openCreatedPdfFile();
    }

    private void openCreatedPdfFile() {
        File pdfFile = new File(context.getExternalFilesDir("PDF"), "report.pdf");
        Uri path = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", pdfFile);
        Log.d(TAG, "" + path);

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(path, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.d(TAG, "viewGeneratePdfFile: " + e.getLocalizedMessage());
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Invoice selecteInvoice) {
        if (mListener != null) {
            mListener.onFragmentInteraction(selecteInvoice);
        }
    }

//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        mContext = context;
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void InvoiceArrayList(ArrayList arrayList) {
        arrayListvcode = arrayList;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Invoice selecteInvoice);
    }

}