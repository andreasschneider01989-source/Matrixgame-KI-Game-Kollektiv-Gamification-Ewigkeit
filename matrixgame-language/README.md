# LUMINI Language – MVP

A first, dependency-free Java 21 prototype for a restricted modelling language.

## Current scope

- project declaration and epistemic status;
- numeric parameters with SI base units `kg`, `m`, and `s`;
- derived expressions using names, parentheses, addition, subtraction, multiplication, and division;
- dimensional checking;
- explicit unit assertions;
- deterministic JSON Model IR output;
- line/column diagnostics;
- no arbitrary Java, shell, file, or network execution from model files.

This is a compiler prototype, not a complete scientific simulator. Unit consistency is necessary but does not prove that a physical equation is scientifically valid.

## Build and test

Requires JDK 21 or newer.

```sh
sh test.sh
```

## Compile a model

```sh
sh run.sh examples/density.lumini
./run.sh examples/density.lumini --out build/density-model-ir.json
```

## Negative example

`examples/invalid-units.lumini` intentionally adds mass and length. The compiler must reject it.

```sh
./run.sh examples/invalid-units.lumini
```

## Example language

```lumini
project "density-demo" {
  claim_status = MODEL;
  parameter mass = 10 kg;
  parameter volume = 2 m^3;
  derive density = mass / volume;
  assert_unit density = kg / m^3;
}
```

## Next steps

1. Add source maps and a complete AST representation.
2. Add JSON Schema validation for Model IR.
3. Add SI prefixes and unit conversion factors.
4. Add a controlled expression function registry.
5. Add the first Matrix Core engine adapter.
6. Add a versioned Run Manifest with seed, commit, engine, and dataset hashes.
7. Add analytical reference tests before making accuracy claims.
