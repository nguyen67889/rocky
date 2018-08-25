.PHONY: all bundle report docs

all: bundle report docs

report:
	mkdir -p ./dist/
	cd report/; pdflatex report.tex
	cd report/; cp report.pdf ../dist/a1-comp3702-44354006-44394918-44341800.pdf

bundle:
	mkdir -p ./dist/
	cd src/; zip -r ../dist/a1-comp3702-44354006-44394918-44341800.zip *

docs:
	javadoc -sourcepath ./src -d ./docs -subpackages .;

clean:
	rm -r ./out/ ./dist/ ./docs/ ./report/report.pdf