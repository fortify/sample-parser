package com.thirdparty;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fortify.plugin.api.BasicVulnerabilityBuilder;
import com.thirdparty.scan.DateDeserializer;
import com.thirdparty.scan.DateSerializer;
import com.thirdparty.scan.Finding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.Inet4Address;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.thirdparty.SscBuiltInVulnerabilityAttribute.*;
import static com.thirdparty.SampleParserVulnerabilityAttribute.*;

/**
 * (C) Copyright 2015,2016 Hewlett Packard Enterprise Development, L.P.
 */
public class ScanGenerator {

    private static final DateSerializer DATE_SERIALIZER = new DateSerializer();
    static final DateDeserializer DATE_DESERIALIZER = new DateDeserializer();
    private static final Charset charset = StandardCharsets.US_ASCII;

    private static final BasicVulnerabilityBuilder.Priority[] PRIORITY_VALUES = BasicVulnerabilityBuilder.Priority.values();
    private static final int PRIORITY_SIZE = PRIORITY_VALUES.length;
    private static final String[] CUSTOM_STATUS_VALUES = {"NEW", "OPEN", "REMEDIATED"};

    private static final String SCAN_TYPE_STEADY = "steady";
    private static final String SCAN_TYPE_GENERIC = "generic";

    private static String scanType;

    enum CustomStatus {
        NEW,
        OPEN,
        REMEDIATED
    }

    private final Random random;
    private final File outputFile;
    private int issueCount;
    private final int categoryCount;
    private final int longTextSize;
    private final Instant now;

    private ScanGenerator(final Random random, final File outputFile, final int issueCount, final int categoryCount, final int longTextSize, final Instant now) {
        this.random = random;
        this.outputFile = outputFile;
        this.issueCount = issueCount;
        this.categoryCount = categoryCount;
        this.longTextSize = longTextSize;
        this.now = now;
    }

    private ScanGenerator(final Random random, final File outputFile) {
        this(random, outputFile, 0, 0, 0, null);
    }

    private static boolean isScanGeneric() {
        return SCAN_TYPE_GENERIC.equals(scanType);
    }

    private static boolean isScanSteady() {
        return SCAN_TYPE_STEADY.equals(scanType);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InterruptedException {
        boolean argsOk = false;
        if ((args.length == 5) || (args.length == 2)) {
            scanType = args[0].toLowerCase();
            if (isScanGeneric() || isScanSteady()) {
                argsOk = true;
            }
        }
        if (!argsOk) {
            System.err.println(String.format("Usage:\n" +
                    "\tjava -cp <class_path> %s " + SCAN_TYPE_STEADY + " <OUTPUT_SCAN_ZIP_NAME>\n" +
                    "\tjava -cp <class_path> %s " + SCAN_TYPE_GENERIC + " <OUTPUT_SCAN_ZIP_NAME> <ISSUE_COUNT> <CATEGORY_COUNT> <LONG_TEXT_SIZE>\n"
                    , ScanGenerator.class.getName(), ScanGenerator.class.getName()));
            System.exit(1);
        }

        ScanGenerator scanGenerator;
        if (isScanSteady()) {
            scanGenerator = new ScanGenerator(SecureRandom.getInstanceStrong(), new File(args[1]));
        } else {
            scanGenerator = new ScanGenerator(SecureRandom.getInstanceStrong(), new File(args[1]), Integer.valueOf(args[2]), Integer.valueOf(args[3]), Integer.valueOf(args[4]), Instant.now());
        }
        scanGenerator.write();
    }

    private void write() throws IOException, InterruptedException {
        if (!outputFile.createNewFile()) {
            System.err.println(String.format("File %s already exists!", outputFile.getPath()));
            System.exit(2);
        }
        try (
            final OutputStream out = new FileOutputStream(outputFile);
            final ZipOutputStream zipOut = new ZipOutputStream(out)
        ) {
            writeScanInfo("SAMPLE", zipOut);
            if (isScanSteady()) {
                writeScan(zipOut, SteadySampleScan.STEADY_FINDINGS::get, SteadySampleScan.STEADY_FINDINGS.size());
            } else {
                writeScan(zipOut, this::generateFinding, issueCount);
            }
        } catch (final Exception e) {
            try {
                Files.delete(outputFile.toPath());
            } catch (final Exception suppressed) {
                e.addSuppressed(suppressed);
            }
            throw e;
        }
        System.out.println(String.format("Scan file %s successfully created.", outputFile.getPath()));
    }

    private static void writeScanInfo(final String engineType, final ZipOutputStream zipOut) throws IOException {
        final Properties scanInfoProps = new Properties();
        scanInfoProps.put("engineType", engineType);
        try (final ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
            scanInfoProps.store(byteOut, "scan.info");
            zipOut.putNextEntry(new ZipEntry("scan.info"));
            zipOut.write(byteOut.toByteArray());
        }
    }

    private void writeScan(final ZipOutputStream zipOut, Function<Integer, Finding> getFinding, Integer findingCount
            ) throws IOException, InterruptedException {

        final long startTime = System.currentTimeMillis();
        final String jsonFileName = isScanSteady() ?   "steady-sample-scan.json" : "generic-sample-scan.json";
        zipOut.putNextEntry(new ZipEntry(jsonFileName));
        try (final JsonGenerator jsonGenerator = new JsonFactory().createGenerator(zipOut)) {
            if (isScanSteady()) {
                jsonGenerator.setPrettyPrinter(new DefaultPrettyPrinter());
            }
            jsonGenerator.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
            jsonGenerator.writeStartObject();
            if (isScanSteady()) {
                jsonGenerator.writeStringField(ENGINE_VERSION.attributeName(), SteadySampleScan.ENGINE_VERSION);
                jsonGenerator.writeStringField(SCAN_DATE.attributeName(), SteadySampleScan.SCAN_DATE);
                jsonGenerator.writeStringField(BUILD_SERVER.attributeName(), SteadySampleScan.BUILD_SERVER);
            } else {
                jsonGenerator.writeStringField(ENGINE_VERSION.attributeName(), "1.0-SNAPSHOT");
                jsonGenerator.writeStringField(SCAN_DATE.attributeName(), DATE_SERIALIZER.convert(new Date()));
                jsonGenerator.writeStringField(BUILD_SERVER.attributeName(), Inet4Address.getLocalHost().getHostName());
            }
            jsonGenerator.writeArrayFieldStart("findings");
            int i;
            for (i = 0; i < findingCount; i++) {
                writeFinding(jsonGenerator, getFinding.apply(i));
            }
            jsonGenerator.writeEndArray();
            // NB: this value should be in seconds, but we always want some non-zero value, so we use millis
            if (isScanSteady()) {
                jsonGenerator.writeNumberField(ELAPSED.attributeName(), (System.currentTimeMillis() - startTime));
            } else {
                jsonGenerator.writeNumberField(ELAPSED.attributeName(), SteadySampleScan.ELAPSED);
            }
            jsonGenerator.writeEndObject();
        }
    }


    private Finding generateFinding(final int i) {
        final String uniqueId = UUID.randomUUID().toString();
        final String id = String.format("%s/%08d", uniqueId, i + 1);
        final int randCat = random.nextInt(categoryCount);

        Finding fn = new Finding();

        // mandatory custom attributes
        fn.setUniqueId(UUID.randomUUID().toString());

        // builtin attributes
        fn.setCategory(String.format("[generated] Random category %d", randCat));
        fn.setFileName(String.format("file-%s.bin", id));
        fn.setVulnerabilityAbstract("Abstract for vulnerability " + id);
        fn.setLineNumber(random.nextInt(Integer.MAX_VALUE));
        fn.setConfidence(random.nextFloat() * 9 + 1); // 1..10
        fn.setImpact(random.nextFloat() + 200f);
        fn.setPriority(PRIORITY_VALUES[random.nextInt(PRIORITY_SIZE)]);

        // custom attributes
        fn.setCategoryId(String.format("c%d", randCat));
        fn.setArtifact(String.format("artifact-%s.jar", id));
        fn.setDescription("Description for vulnerability " + id + "\nSecurity problem in code...");
        fn.setComment("Comment for vulnerability " + id + "\nMight be a false positive...");
        fn.setBuildNumber(String.valueOf(random.nextFloat() + 300f));
        fn.setCustomStatus(CUSTOM_STATUS_VALUES[random.nextInt(CUSTOM_STATUS_VALUES.length)]);
        fn.setLastChangeDate(Date.from(now.minus(2, ChronoUnit.DAYS).minus(2, ChronoUnit.HOURS)));
        fn.setArtifactBuildDate(Date.from(now.minus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.HOURS)));
        fn.setText64("Very long text for " + id + ": \n");

        return fn;
    }

    private void writeFinding(final JsonGenerator jsonGenerator, final Finding fn) throws IOException, InterruptedException {
        jsonGenerator.writeStartObject();

        // Mandatory custom attributes
        jsonGenerator.writeStringField(UNIQUE_ID.attributeName(), fn.getUniqueId());

        // Builtin attributes
        jsonGenerator.writeStringField(CATEGORY.attributeName(), fn.getCategory());
        jsonGenerator.writeStringField(FILE_NAME.attributeName(), fn.getFileName());
        jsonGenerator.writeStringField(VULNERABILITY_ABSTRACT.attributeName(), fn.getVulnerabilityAbstract());
        jsonGenerator.writeNumberField(LINE_NUMBER.attributeName(), fn.getLineNumber());
        jsonGenerator.writeNumberField(CONFIDENCE.attributeName(), fn.getConfidence());
        jsonGenerator.writeNumberField(IMPACT.attributeName(), fn.getImpact());
        jsonGenerator.writeStringField(PRIORITY.attributeName(), fn.getPriority().toString());

        // Custom attributes
        jsonGenerator.writeStringField(CATEGORY_ID.attributeName(), fn.getCategoryId());
        jsonGenerator.writeStringField(CUSTOM_STATUS.attributeName(), fn.getCustomStatus());
        jsonGenerator.writeStringField(ARTIFACT.attributeName(), fn.getArtifact());
        jsonGenerator.writeStringField(DESCRIPTION.attributeName(), fn.getDescription());
        jsonGenerator.writeStringField(COMMENT.attributeName(), fn.getComment());
        jsonGenerator.writeStringField(BUILD_NUMBER.attributeName(), fn.getBuildNumber());
        jsonGenerator.writeStringField(LAST_CHANGE_DATE.attributeName(), DATE_SERIALIZER.convert(fn.getLastChangeDate()));
        jsonGenerator.writeStringField(ARTIFACT_BUILD_DATE.attributeName(), DATE_SERIALIZER.convert(fn.getArtifactBuildDate()));
        jsonGenerator.writeFieldName(TEXT64.attributeName());
            writeLoremIpsum(fn.getText64(), jsonGenerator);

        jsonGenerator.writeEndObject();
    }

    private void writeLoremIpsum(final String name, final JsonGenerator jsonGenerator) throws InterruptedException, IOException {
        final int size = longTextSize + name.length();
        try (final InputStream in = getLoremIpsum(name, size)) {
            jsonGenerator.writeBinary(in, size);
        }
    }

    private static InputStream getLoremIpsum(final String name, final int size) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final PipedInputStream in = new PipedInputStream();
        try {
            final Thread t = new Thread(() -> pipedStreamProducer(name, in, latch, size));
            t.setDaemon(true);
            t.start();
            if (latch.await(10, TimeUnit.SECONDS)) {
                return in;
            } else {
                t.interrupt();
                throw new RuntimeException("Timeout while waiting for latch for " + name);
            }
        } catch (final Exception e) {
            try {
                in.close();
            } catch (final Exception suppressed) {
                e.addSuppressed(suppressed);
            }
            throw e;
        }
    }

    private static void pipedStreamProducer(final String name, final PipedInputStream in, final CountDownLatch latch, final int size) {
        try (final PipedOutputStream out = new PipedOutputStream(in)) {
            latch.countDown();
            int written = min(name.length(), size);
            out.write(name.getBytes(charset), 0, written);
            final int loremIpsumLen = LOREM_IPSUM.length;
            while (written < size) {
                final int len = min(loremIpsumLen, size - written);
                out.write(LOREM_IPSUM, 0, len);
                written += len;
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private static int min(final int i, final int j) {
        return i < j ? i : j;
    }

    private static byte[] LOREM_IPSUM = (
        "Lorem ipsum dolor sit amet, eam ridens cetero iuvaret id. Ius eros fabulas ei. Te vis unum intellegam, cu sed ullum eruditi, et est lorem volumus. Te altera malorum quaestio mei, sea ea veniam disputando.\n" +
        "\n" +
        "Illud labitur definitionem ut sit, veri illum qui ut. Ludus patrioque voluptaria pri ad. Magna mundi voluptatum his ea. His paulo possim ea, et vide omittam philosophia sit. Eu lucilius legendos incorrupte eos, eu falli molestie argumentum cum.\n" +
        "\n" +
        "Melius torquatos ea his. Movet dolorem cu eam. Nisl offendit repudiare ne est. No veri appareat petentium eum.\n" +
        "\n" +
        "Duo in omnium accumsan legendos. Pro id probo oportere salutatus, sonet omnium epicurei eu pri. Indoctum disputando ei sea, an viris legere delicatissimi vix, ne dico melius admodum eam. Pro nostro inimicus liberavisse an. Id pro nostrum theophrastus, his et liber iusto docendi, purto convenire tincidunt pri an.\n" +
        "\n" +
        "Ex ubique accusamus est. Te sumo persecuti mei. Ne veniam mollis mei, natum perfecto definitionem at has. Liber honestatis ad cum, porro expetendis conclusionemque per eu. Suscipit dissentiet per an, ad usu sumo homero debitis. At eam quando placerat, nonumy forensibus scripserit at pro.\n" +
        "\n" +
        "Vero quodsi no usu, usu nisl erat iracundia in. Sed te habeo viris graeco. Persius admodum sententiae no eam, ut dicunt erroribus sit. Dolorum appetere legendos et qui. Vim ei feugait perfecto sadipscing.\n" +
        "\n" +
        "Nam audire detracto et, epicurei suscipiantur at his, vis id veri dolor. Pro id insolens singulis, accumsan singulis eam at, qui cu diam ceteros singulis. Atqui graecis fastidii cu mei. Invidunt singulis ex eam, et detracto hendrerit sadipscing quo. Est ne graeci vidisse placerat, wisi appareat erroribus ius an. Ea vim dicam aperiri. Ex elit aliquid est, nostro intellegam mel te.\n" +
        "\n" +
        "Eu per consul semper vituperatoribus, odio dicat audiam eam ea. Qui ei iisque nonumes repudiare, fugit quidam eu sit. An eam debet concludaturque, in nostro meliore splendide quo, ei est eros accumsan scribentur. Ei idque dolore honestatis sea. Tollit convenire salutatus ex mea, quem tantas epicurei in usu.\n" +
        "\n" +
        "Nam at cibo nominati, ne meis harum per, eu cum brute saepe veniam. Quo fabulas insolens cu, vix ne animal detraxit. Adhuc paulo similique ut eam, cu sit persius phaedrum. Cu eruditi periculis salutatus est, dicam veniam verterem ius at.\n" +
        "\n" +
        "Everti vivendum splendide ad qui, ad quod nominavi comprehensam quo, mollis scripta eu eum. Te pro dicta volumus, his affert ornatus dissentias id. Mea no quot referrentur, an his eius eripuit noluisse. His eu legere eruditi."
    ).getBytes(charset);

}
