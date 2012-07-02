@echo OFF
set CLASSPATH=.;lib/*;build/classes
@echo ON
java dk.dma.aiscoverage.AisCoverage %*
