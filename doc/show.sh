#! /bin/bash
rm -f *.aux *.dvi *.log
latex collision.tex
latex collision.tex
dvips collision.dvi
ps2pdf collision.ps
gv collision.ps &
