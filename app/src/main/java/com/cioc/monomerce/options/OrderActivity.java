package com.cioc.monomerce.options;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cioc.monomerce.backend.BackendServer;
import com.cioc.monomerce.R;
import com.cioc.monomerce.entites.Order;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class OrderActivity extends AppCompatActivity {
    Context context;
    AsyncHttpClient client;
    public static ArrayList<Order> orderList;
    private LinearLayoutManager manager;
    private Boolean isScrolling = false;
    private int currentItems, totalItems, scrollOutItems;
    private String token = "";
    private OrderRecyclerViewAdapter adapter;
    private ProgressBar orderProgressBar;
    private RecyclerView orderRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = OrderActivity.this;
        setContentView(R.layout.activity_order);
        BackendServer backend = new BackendServer(context);
        client = backend.getHTTPClient();
        orderList = new ArrayList<>();
        orderRecyclerView = findViewById(R.id.order_list);
        orderProgressBar = findViewById(R.id.progressBar_order);
        getOrderHistory();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_arrow_back_white_24dp, getApplicationContext().getTheme());
        toggle.setHomeAsUpIndicator(drawable);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        orderProgressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                orderProgressBar.setVisibility(View.GONE);
                manager = new LinearLayoutManager(OrderActivity.this);
                orderRecyclerView.setLayoutManager(manager);

                adapter = new OrderRecyclerViewAdapter(orderList);
                orderRecyclerView.setAdapter(adapter);
            }
        },2000);

//        orderRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
//                    isScrolling = true;
//                }
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                currentItems = manager.getChildCount();
//                totalItems = manager.getItemCount();
//                scrollOutItems = manager.findFirstVisibleItemPosition();
//
//                if(isScrolling && (currentItems + scrollOutItems == totalItems)) {
//                    orderProgressBar.setVisibility(View.VISIBLE);
//                    isScrolling = false;
//                    getOrderHistory();
//
//                }
//            }
//        });

    }

    public void getOrderHistory(){
        client.get(BackendServer.url+"/api/ecommerce/order/?&Name__contains=&offset=0", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                try {
//                    JSONArray response = response1.getJSONArray("results");
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        Order order = new Order(object);
                        orderList.add(order);
                        orderProgressBar.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                        e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(OrderActivity.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }

    private class OrderRecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder>{
        BackendServer backend = new BackendServer(context);
        AsyncHttpClient client = backend.getHTTPClient();
        ArrayList<Order> orderArrayList;

        public OrderRecyclerViewAdapter(ArrayList<Order> orders) {
            this.orderArrayList = orders;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order_history, parent, false);
            return new ViewHolder(view);
        }

//        @Override
//        public void onViewRecycled(ViewHolder holder) {
//            if (holder.mImageView.getController() != null) {
//                holder.mImageView.getController().onDetach();
//            }
//            if (holder.mImageView.getTopLevelDrawable() != null) {
//                holder.mImageView.getTopLevelDrawable().setCallback(null);
////                ((BitmapDrawable) holder.mImageView.getTopLevelDrawable()).getBitmap().recycle();
//            }
//        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
            final Order order = orderArrayList.get(i);
            viewHolder.orderId.setText("#"+order.getPk());
            viewHolder.totalAmount.setText("Rs. "+order.getTotalAmount());
            viewHolder.approved.setText(order.getApproved());
            viewHolder.status.setText(order.getStatus());
            viewHolder.paymentMode.setText(order.getPaymentMode());

            viewHolder.cardViewOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(context, OrderDetailsActivity.class)
                            .putExtra("address", order.getStreet()+"\n"+order.getLandMark()+"\n"+order.getCity()+", "
                                    +order.getState()+", "+order.getCountry()+", "+order.getPincode()+", "+order.getCountry()+"\n"+order.getMobileNo())
                    .putExtra("pos", i)
                    .putExtra("pk", order.getPk()));
                }
            });

            viewHolder.downloadInvoices.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    new DownloadFile().execute("http://maven.apache.org/maven-1.x/maven.pdf", "maven.pdf");
                    client.get(BackendServer.url+"/api/ecommerce/downloadInvoice/?value="+order.getPk(), new FileAsyncHttpResponseHandler(context) {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                            Toast.makeText(OrderActivity.this, "onFailure", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, File response) {
                            // Do something with the file `response`
                            Bitmap bitmap = BitmapFactory.decodeFile(response.getAbsolutePath());
//                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                            bitmap.compress(Bitmap.CompressFormat.PNG, 100 /*ignored for PNG*/, bos);
//                            byte[] bitmapdata = bos.toByteArray();

//                            final int  MEGABYTE = 1024 * 1024;
//                            File file1 = new File(Environment.getExternalStorageDirectory()+"/"+getString(R.string.app_name1));
//                            File folder = new File(file1.getAbsolutePath());
////                            folder.mkdir();
//                            File pdfFile = new File(folder, "invoice_"+ Calendar.getInstance().getTime());
//                            try {
////                                FileInputStream fis = new FileInputStream(file);
////                                pdfFile.createNewFile();
//                                String path = pdfFile.getAbsolutePath() + ".pfd";
//                                FileOutputStream fileOutputStream = new FileOutputStream(path);
//
//                                fileOutputStream = new FileOutputStream(path);
//                                fileOutputStream.write(bitmapdata);
//                                fileOutputStream.flush();
//                                fileOutputStream.close();
////                                byte[] buffer = new byte[MEGABYTE];
////                                int bufferLength = 0;
////                                while((bufferLength = fis.read(buffer))>0 ){
////                                    fileOutputStream.write(buffer, 0, bufferLength);
////                                }
//                            } catch (IOException e){
//                                e.printStackTrace();
//                            }
                            Document document = new Document();
                            String directoryPath = android.os.Environment.getExternalStorageDirectory().toString();
                            try {
                                PdfWriter.getInstance(document, new FileOutputStream(Environment.getExternalStorageDirectory()+"/"+getString(R.string.app_name1)+ "/invoice_"+ Calendar.getInstance().getTime()+ ".pdf")); //  Change pdf's name.
                                document.open();
                                Image image = Image.getInstance(response.getAbsolutePath());  // Change image's name and extension.
                                float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                                    - document.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.image.scalePercent(scaler);image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
                                document.add(image);
                                document.close();
                            } catch (DocumentException e) {
                                e.printStackTrace();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

//                        convertText(response.getAbsolutePath(), Environment.getExternalStorageDirectory()+"/"+getString(R.string.app_name1)+ "/invoice_"+ Calendar.getInstance().getTime()+ ".pdf");
                            Toast.makeText(OrderActivity.this, "onSuccess", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

        @Override
        public int getItemCount() {
            return orderArrayList.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder{
        TextView orderId, totalAmount, approved, status, paymentMode;
        ImageView orderInfo, downloadInvoices;
        CardView cardViewOrder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_no_result);
            totalAmount = itemView.findViewById(R.id.total_amount_result);
            approved = itemView.findViewById(R.id.approved_result);
            status = itemView.findViewById(R.id.status_result);
            paymentMode = itemView.findViewById(R.id.payment_mode_result);
            cardViewOrder = itemView.findViewById(R.id.card_view_order);
            downloadInvoices = itemView.findViewById(R.id.downloads_invoice_action);
        }
    }
    private void convertText(String textFilePath, String outputPath) {
        FileInputStream fis = null;
        DataInputStream in = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(outputPath));
            document.open();
            File file = new File(textFilePath);
            if (file.exists()) {
                fis = new FileInputStream(file);
                in = new DataInputStream(fis);
                isr = new InputStreamReader(in);
                br = new BufferedReader(isr);
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    Paragraph para = new Paragraph(strLine + "\n");
                    para.setAlignment(Element.ALIGN_JUSTIFIED);
                    document.add(para);
                }
                showAlertDialog("Converting text...", "Converting text to PDF finished... Generated PDF saved in " + outputPath);
            } else {
                showAlertDialog("Converting text...", "File " + textFilePath + " does not exist!");
            }
            document.close();
        } catch (Exception e) {
            showAlertDialog("Converting text...", "An error has occurred: " + e.getMessage());
        }
    }
    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderActivity.this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }
}
