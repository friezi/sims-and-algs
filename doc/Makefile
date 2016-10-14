.PHONY: all show clean

all: clean
	latex collision.tex
	latex collision.tex
	dvips collision.dvi
	ps2pdf collision.ps

clean:
	rm -f *.aux *.dvi *.log *.ps

show: all
	gv collision.ps

ed:
	emacs *.tex Makefile &
