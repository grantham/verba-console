# Verba
Verba is a console-based Latin-English dictionary application based on the public
domain dictionary [An Elementary Latin Dictionary (1895)](http://www.archive.org/details/anelementarylat01lewigoog)
by Charlton T. Lewis.

Verba provides a console-based Latin-English dictionary in which a search may be conducted using any form of a word. For
instance, if you were to search for the word _arma_, verba would return:

    arma (arma):
	    - noun nom neut pl
	    - noun voc neut pl
        - noun acc neut pl
    1. arma, ōrum; noun neut
        1 AR-, implements, outfit, [rest elided for brevity]

    arma (armo):
        - verb second person sg pres imperat act
    1. armo, āuī, ātus, āre;
        arma, to furnish with weapons [rest elided for brevity]

Lewis' _An Elementary Latin Dictionary_ was digitized by the [Perseus Project](www.perseus.tufts.edu) by encoding this dictioanry into TEI XML.
The Perseus project also has generated morphological forms from the text corpus, and this is available in the
[Perseus Hopper source](https://sourceforge.net/projects/perseus-hopper/).

## License

- The file `/org/perseus/lexicon/lewis.xml` is licensed under the
[Creative Commons ShareAlike 3.0 License](http://creativecommons.org/licenses/by-sa/3.0/us/),
according to the Perseus Project [download page](http://www.perseus.tufts.edu/hopper/opensource/download).
- The file `/org/perseus/lexicon/latin.morph.xml` is distributed with the
[Perseus Hopper source](https://sourceforge.net/projects/perseus-hopper/), which is (according only to the sourceforge
project listing) licensed under the [Mozilla Public License 1.1 (MPL 1.1)](http://www.mozilla.org/MPL/MPL-1.1.html).
The [Perseus Hopper project page](http://perseus-hopper.sourceforge.net/) claims that "_...The exact license terms used
by this project on their project summary page  and in the licensing documents included in their downloads_", but I have
found nothing detailing the license anywhere in the source distribution.

I would prefer to release under the GPL, but since Perseus won't dual-license I'm releasing this software under the
[Mozilla Public License (MPL) 1.1](http://www.mozilla.org/MPL/MPL-1.1.html) using a provision (section 13) permitting
licensing under the GPL. The only files not covered by this license are the two listed above.

## Source and pre-built binaries
_Source_ is available from the verba-console git repository: [https://github.com/grantham/verba-console](https://github.com/grantham/verba-console)

Pre-built _binaries_ are available from [http://www.magnopere.org](http://www.magnopere.org)

## Prerequisites
The following must be installed to run:

- A recent Java (6 or later)
- [SQLite 3](http://www.sqlite.org/download.html) (the `sqlite3` command must be in your system path).

In order to build, you'll need:

- A recent JDK (6 or later)
- [SQLite 3](http://www.sqlite.org/download.html) (the `sqlite3` command must be in your system path).
- ant

## Building

1. call:

        $ ant -Dinstallation.dir=/home/you/bin/verba-1.0.0 -Dproject.version=1.0.0 install

This will build the jar, a script to start the application, and the database.

Both of the `-D` parameters are optional:

- if `installation.dir` is not specified, `install` will create a `./verba-installation` directory and install there.
- if `project.version` us bit specified, the current version + _-SNAPSHOT- is used.


## Installing

### From source:
Simply run the ant `install` target, passing in the path to the desired installation directory:

        $ ant -Dinstallation.dir=/path/to/installation/dir install

### From pre-built binaries
Download the distribution zip file from [http://www.magnopere.org](http://www.magnopere.org) and unzip in your preferred
location.

For convenience, either add the `verba.sh` script to your system path or create a script in your system path which
changes to the verba-console installation directory and invokes the `verba.sh` script.

## Running

If you did not put a `verba.sh` (or a script which invokes it) in your system path:

1. `$ cd /path/to/installation/dir`
2. `$ ./verba.sh`

Otherwise, just enter the script name you created in the console.

When verba starts, simply enter any form of a latin word you wish to look up at the prompt:

    $ uerba> arma

Spell the word however you wish, as
Verba will internally rectify the orthography to one using the consonantal u/i system.

The form of a word will be mapped to one or more _lemmas_ (i.e. terms in a dictionary). The part of speech and
matching inflectional forms will be listed for each matching lemma along with the definition for each lemma.

For instance, if you entered `arma`

    $ uerba> arma

you would see this result:

    arma (arma):
	    - noun nom neut pl
	    - noun voc neut pl
        - noun acc neut pl
    1. arma, ōrum; noun neut
        1 AR-, implements, outfit, [rest elided for brevity]

    arma (armo):
        - verb second person sg pres imperat act
    1. armo, āuī, ātus, āre;
        arma, to furnish with weapons [rest elided for brevity]

The form `arma` matches two lemmas: a noun, and a verb.

* For the noun, we list three inflectional noun forms which `arma` matches, then procede to give the definition.
* For the verb, we list the single inflectional verb form that `arma` matches as a verb, then procede to give the definition.
