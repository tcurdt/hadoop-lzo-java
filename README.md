# LZO index files without the hassle

For LZO files to be splittable for Hadoop they require an accompanied index file. One project does this job is the [hadoop-lzo](https://github.com/kevinweil/hadoop-lzo).
Unfortunately it depends the native libraries via JNI. In other words - pain just around the corner. As creating the index merely needs more than seeking through the LZO file, there really is no reason for not doing this without all these dependencies.

## How to get it

The jars are available on [maven central](http://repo1.maven.org/maven2/org/vafer/lzo-index/).
The source releases you can get in the [download section](http://github.com/tcurdt/lzo-index/downloads).

If feel adventures or want to help out feel free to get the latest code
[via git](http://github.com/tcurdt/lzo-index/tree/master).

    git clone git://github.com/tcurdt/lzo-index.git

## How to use

You can either use the jar directly from the command line

    > java -jar lzo-index.jar -v file.lzo
    file.lzo -> file.lzo.index

Or you can use the 'LzoIndexer' class directly

    LzoIndexer indexer = new LzoIndexer();
    indexer.createIndex(
      new FileInputStream("file.lzo"),
      new FileOutputStream("file.lzo.index"));

## How to build

    mvn clean install

## License

I wish I could release this under ASL 2.0 but unfortunately the LZO code is under GPL which is why one could argue this is derived work ...and therefor also has to be GPL. Feel free to convince [Markus](http://www.oberhumer.com/) to at least change the license to LGPL. Anyway - for now it has to be GPLv2.