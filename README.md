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

## Repository
The verba-console git repository is available at [https://github.com/grantham/verba-console](https://github.com/grantham/verba-console)

## Prerequisites
The following must be installed to build and run:

- A recent Java (6 or later)
- [SQLite 3](http://www.sqlite.org/download.html)

## Building

1. call:

        $ ant -D/path/to/installation/dir install

This will build the jar, a script to start the application, and the database.


## Installing

Assuming you have already run the ant install target:

1. `$ cd /path/to/installation/dir`
2. `$ chmod a+x ./verba.sh`

For convenience, add a symlink somewhere in your system path to the verba.sh script.

## Running

If you did not put a symlink in your path to the verba.sh

1. `$ cd /path/to/installation/dir`
2. `$ ./verba.sh`

Otherwise, just enter the symlink name you created in the console.

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
