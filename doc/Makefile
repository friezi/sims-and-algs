.PHONY: all show clean ps

ps:
	latex collision.tex
	latex collision.tex
	dvips collision.dvi

pdf: ps
	ps2pdf collision.ps

all: pdf clean

clean:
	rm -f *.aux *.dvi *.log *.ps

show: ps
	gv collision.ps

ed:
	emacs *.tex Makefile &
