#!/bin/bash
# Uso: docs/llm/evaluar.sh T0x   → compila, corre los tests propios, compara firmas y corre los ocultos de T0x.
cd "$(dirname "$0")/../.." || exit 1
T=$1
FIRMAS=docs/llm/evaluacion/firmas
EV=docs/llm/evaluacion/java/com/finanzas
echo "== tests propios (todos)"
mvn -B -q -o test > /tmp/evaluar-fin-$USER.log 2>&1; echo "mvn test: exit $?"
grep -E "Tests run:|COMPILATION ERROR|ERROR\] /|expected:|but was|Exception:|\.java:[0-9]+\)" /tmp/evaluar-fin-$USER.log | grep -v "^\s*at org\.\|^\s*at java\." | head -30
echo "== firmas"
for f in "$FIRMAS"/*.txt; do
  c=$(basename "$f" .txt)
  javap -p -cp target/classes "$c" 2>/dev/null | grep -vF 'lambda$' | diff - <(grep -vF 'lambda$' "$f") > /dev/null || echo "CAMBIÓ: $c"
done
echo "== ocultos $T"
cp $EV/service/${T}[A-Z]*OcultoTest.java src/test/java/com/finanzas/service/ 2>/dev/null
cp $EV/cli/${T}[A-Z]*OcultoTest.java src/test/java/com/finanzas/cli/ 2>/dev/null
rm -f target/surefire-reports/*${T}*Oculto*
mvn -B -o test -Dtest="${T}*OcultoTest" -Dsurefire.failIfNoSpecifiedTests=false > /tmp/evaluar-fin-oculto-$USER.log 2>&1
grep -E "Tests run:.*Oculto|COMPILATION ERROR|ERROR\] /" /tmp/evaluar-fin-oculto-$USER.log | head -10
grep -h -E "^Tests run|<<<|expected" target/surefire-reports/*${T}*OcultoTest.txt 2>/dev/null | head -20
rm -f src/test/java/com/finanzas/service/T0*OcultoTest.java src/test/java/com/finanzas/cli/T0*OcultoTest.java
