package helper;
//PDF

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;


import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.koruja.notecam.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import model.Foto;
import model.Materia;
import model.Topico;

public class PdfCreator {
    //PDF Variables Teste
    //pdf class variables

    private Materia materia;
    private List<Topico> topicos;
    PdfWriter writer;
    BaseColor cor_materia;
    int MARGIN = 20;
    int progresso = 0;

    private static String file;
    //private static String FILE = Singleton.NOTECAM_FOLDER + File.separator+ "p""firstPdf.pdf";
    //private static String FILE = Environment.getExternalStorageDirectory()+ File.separator+"firstPdf.pdf";
    public static final Chunk NEWLINE = new Chunk("\n");

    private static Font font_nome_da_materia = new Font(Font.FontFamily.HELVETICA, 65,
            Font.NORMAL, BaseColor.WHITE);

    private static Font font_topico = new Font(Font.FontFamily.HELVETICA, 26,
            Font.NORMAL, BaseColor.WHITE);

    private static Font font_rodape = new Font(Font.FontFamily.HELVETICA, 20,
            Font.NORMAL, BaseColor.WHITE);

    private static Font font_black_transparente = new Font(Font.FontFamily.HELVETICA, 20,
            Font.NORMAL, new BaseColor(47, 47, 47));

    private static Font font_black_transparente_14 = new Font(Font.FontFamily.HELVETICA, 14,
            Font.ITALIC, new BaseColor(47, 47, 47));

    private static Font font_black_transparente_30 = new Font(Font.FontFamily.HELVETICA, 30,
            Font.NORMAL, new BaseColor(47, 47, 47));


    public void criarPDF(Materia materia, List<Topico> topicos) {
        this.materia = materia;
        this.topicos = topicos;


        //Typeface mFace = Typeface.createFromAsset(Singleton.getMateriasActivity().getAssets(), "fonts/fontType.ttf");


        this.file = Singleton.NOTECAM_FOLDER + File.separator + this.materia.getOriginal_name() + File.separator + this.materia.getName() + ".pdf";
        Singleton.getAsyncTask().updatePathGerado(file);

        //font_nome_da_materia.setColor(new BaseColor(materia.getColor()));

        //PDF TESTE
        try {
            Document document = new Document();
            writer = PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            document.setMargins(MARGIN,MARGIN,MARGIN,MARGIN);
            addMetaData(document);
            updateProgress(5);
            addTitlePage(document);
            addContent(document);
            document.close();


        } catch (Exception e) {
            Singleton.getMateriasActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(Singleton.getMateriasActivity(), Singleton.getMateriasActivity().getString(R.string.erro_ao_gerar_pdf), Toast.LENGTH_SHORT).show();

                }
            });
            e.printStackTrace();
        }


    }

    private void updateProgress(int i){
        progresso = i;
        Singleton.getAsyncTask().updateProgress(progresso);
    }

    private void addMetaData(Document document) {
        document.addTitle(this.materia.getName());
        document.addSubject(Singleton.getMateriasActivity().getString(R.string.fotos_de) + this.materia.getName());
        document.addKeywords(Singleton.getMateriasActivity().getString(R.string.fotos__) + this.materia.getName());
        document.addAuthor("Notecam");
        document.addCreator("Notecam");
    }

    private void addTitlePage(Document document)
            throws DocumentException, IOException {

        //PdfStamper stamper = new PdfStamper(reader, os);
        //Muda cor da pagina inicial para a cor da matéria
        cor_materia = new BaseColor(materia.getColor());
        mudar_cor_pagina(cor_materia);

        //Adicionar Data
        Calendar calendar = Calendar.getInstance();
        DateFormat formataData = DateFormat.getDateInstance();
        Paragraph p_data = new Paragraph(formataData.format(calendar.getTime()) + "", font_black_transparente);
        p_data.setAlignment(Element.ALIGN_RIGHT);
        document.add(p_data);

        updateProgress(7);

        Paragraph preface = new Paragraph();


        //Adicionar Título (Nome da matéria)
        addEmptyLine(preface, 8);
        Paragraph titulo = new Paragraph(this.materia.getName(), font_nome_da_materia);
        titulo.setAlignment(Element.ALIGN_CENTER);
        preface.add(titulo);

        updateProgress(10);


        //Adicionar "Topicos abordados"
        addEmptyLine(preface, 2);
        font_black_transparente.setSize(24);
        Paragraph subtitulo = new Paragraph("      " + Singleton.getMateriasActivity().getString(R.string.topicos_abordados), font_black_transparente_30);
        preface.add(subtitulo);

        updateProgress(14);


        addEmptyLine(preface, 1);

        updateProgress(15);


        preface.add(create_topicos_title());
        addEmptyLine(preface, 10);
        updateProgress(18);

        preface.add(create_logo_rodape());

        document.add(preface);



        // Start a new page
        document.newPage();

        updateProgress(20);
    }

    public PdfPTable create_topicos_title() throws DocumentException, IOException {
        //Pegar Topico Icon Image
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeResource(Singleton.getMateriasActivity().getResources(), R.drawable.ic_tick);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        Image myImg = Image.getInstance(out.toByteArray());
        myImg.scaleToFit(35, 35);

        //Adicionar Topicos Table
        PdfPTable table = new PdfPTable(2);
        float[] columnWidths = new float[]{1f, 9f};
        table.setWidths(columnWidths);

        for (Topico topico : this.topicos) {
            //Celula do Icone
            PdfPCell cell = new PdfPCell();
            cell.setFixedHeight(38);
            cell.setBorderWidth(0);
            Paragraph p = new Paragraph();
            p.add(new Chunk(myImg, 0, -30));
            cell.addElement(p);
            table.addCell(cell);

            //Celula do topico
            PdfPCell cell2 = new PdfPCell();
            cell2.setBorderWidth(0);
            Phrase nome = new Phrase(topico.getName(), font_topico);
            Phrase numero_fotos = new Phrase("   (" + topico.getFotos().size() + " " + Singleton.getMateriasActivity().getString(R.string.fotos) + ")", font_black_transparente_14);
            Paragraph p2 = new Paragraph(new Phrase(topico.getName(), font_topico));
            p2.add(numero_fotos);
            cell2.addElement(p2);
            table.addCell(cell2);
        }

        return table;
    }

    public PdfPTable create_logo_rodape() throws DocumentException, IOException {
        //Pegar Topico Icon Image
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeResource(Singleton.getMateriasActivity().getResources(), R.drawable.ic_launcher_red);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        Image myImg = Image.getInstance(out.toByteArray());
        myImg.scaleToFit(70, 70);

        //Adicionar Topicos Table
        PdfPTable table = new PdfPTable(2);
        float[] columnWidths = new float[]{1f, 4f};
        table.setWidths(columnWidths);

        //Celula do Icone
        PdfPCell cell = new PdfPCell();
        cell.setFixedHeight(70);
        cell.setBorderWidth(0);
        Paragraph p = new Paragraph();
        p.add(new Chunk(myImg, 0, -55));
        cell.addElement(p);
        table.addCell(cell);

        //Celula do topico
        PdfPCell cell2 = new PdfPCell();
        cell2.setBorderWidth(0);
        Phrase link = new Phrase("www.notecam.co", font_rodape);
        Paragraph p2 = new Paragraph(new Phrase(Singleton.getMateriasActivity().getString(R.string.gerado_pelo_app_notecam), font_rodape));
        p2.add(NEWLINE);
        p2.add(link);
                cell2.addElement(p2);
        table.addCell(cell2);

        table.setHorizontalAlignment(Element.ALIGN_BOTTOM);

        return table;
    }

    private void addContent(Document document) throws DocumentException, IOException {


        int height = 80;

        int progresso_por_topico = new Float(80 / this.topicos.size()).intValue();

        for(Topico topico : this.topicos) {
            int i = 0;
            int progresso_por_foto;

            if(topico.getFotos().size() > 0)
                progresso_por_foto = new Float(progresso_por_topico / topico.getFotos().size()).intValue();
            else
                progresso_por_foto = 1;

            for(Foto foto : topico.getFotos()) {
                i++;
                Rectangle title_back = new Rectangle(MARGIN, document.getPageSize().getHeight() - MARGIN, document.getPageSize().getWidth() - MARGIN, document.getPageSize().getHeight() - MARGIN - height);
                title_back.setBackgroundColor(cor_materia);

                PdfContentByte cb = writer.getDirectContentUnder();
                cb.saveState();
                cb.setColorStroke(cor_materia);
                cb.rectangle(title_back);
                cb.stroke();
                cb.restoreState();

                Paragraph content = new Paragraph();

                absText(topico.getName() + " - " + i, 40, document.getPageSize().getHeight() - MARGIN - height + 20, 40);

                //Adicionar Nome tópico
                //Paragraph nome_topico = new Paragraph("", font_topico_40);


                addEmptyLine(content, 3);
                content.add(NEWLINE);


                //Adicionar foto
                String path = foto.getPath();

                Image image = null;
                try {
                    image = Image.getInstance(path);
                    float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                            - document.rightMargin() - MARGIN) / image.getWidth()) * 100;

                    image.scalePercent(scaler);
                    image.setAlignment(Element.ALIGN_CENTER);
                    content.add(image);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                document.add(content);
                document.newPage();
                updateProgress(progresso + progresso_por_foto);
            }

        }


    }




    private void absText(String text, int x, float y, float size) throws IOException, DocumentException {
        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

        PdfContentByte cb = writer.getDirectContent();
            cb.saveState();
            cb.beginText();
            cb.moveText(x, y);
            cb.setFontAndSize(bf, size);
            cb.setColorFill(BaseColor.WHITE);
            cb.showText(text);
            cb.endText();
            cb.restoreState();

    }


    private void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    private void mudar_cor_pagina(BaseColor baseColor) {
        Rectangle page = new Rectangle(PageSize.A4);
        page.setBackgroundColor(baseColor);

        PdfContentByte cb = writer.getDirectContentUnder();
        cb.saveState();
        cb.setColorStroke(baseColor);
        cb.rectangle(page);
        cb.stroke();
        cb.restoreState();

    }

    private File createFileFromInputStream(InputStream inputStream) {

        try {
            File f = new File("font");
            OutputStream outputStream = new FileOutputStream(f);
            byte buffer[] = new byte[1024];
            int length = 0;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return f;
        } catch (IOException e) {
            //Logging exception
        }

        return null;
    }


    //FIM DO PDF -------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------
}
