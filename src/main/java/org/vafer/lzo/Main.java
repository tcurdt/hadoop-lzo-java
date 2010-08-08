package org.vafer.lzo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class Main {

    @Parameter(description = "file1.lzo file2.lzo ...")
    private List<String> files = new ArrayList<String>();

    @Parameter(names = "-v", description = "verbose output")
    public boolean debug = false;

    private void run() throws Exception {
        for (String file : files) {
            LzoIndexer indexer = new LzoIndexer();
            if (debug) {
                System.out.println(file + " -> " + file + ".index");
            }
            InputStream input = null;
            OutputStream output = null;
            try {
                input = new FileInputStream(file);
                output = new FileOutputStream(file + ".index");
                indexer.createIndex(input, output);
            } finally {
                IOUtils.closeQuietly(input);
                IOUtils.closeQuietly(output);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Main m = new Main();

        JCommander cli = new JCommander(m);
        try {
            cli.parse(args);
        } catch (Exception e) {
            cli.usage();
            System.exit(1);
        }

        m.run();
    }
}
