"""Command-line interface for the LUMINI formula compiler."""
from __future__ import annotations

import argparse
import json
import sys
from pathlib import Path

from .compiler import CompileError, compile_file, dump_json, run_ir


def _parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(prog="lumini", description="Compile and run safe LUMINI formula models.")
    subparsers = parser.add_subparsers(dest="command", required=True)
    for command in ("check", "compile", "run"):
        command_parser = subparsers.add_parser(command)
        command_parser.add_argument("file", type=Path, help="LUMINI source file")
        command_parser.add_argument("-o", "--output", type=Path, help="JSON output path")
    return parser


def main(argv: list[str] | None = None) -> int:
    args = _parser().parse_args(argv)
    try:
        ir = compile_file(args.file)
        if args.command == "check":
            print(f"PASS: {ir['model']['id']} v{ir['model']['version']}")
            print(f"Definitions: {len(ir['definitions'])}; outputs: {', '.join(ir['outputs'])}")
            print("Syntax, symbol resolution and dimensional analysis passed.")
            print("Scientific validation: NOT_PERFORMED")
            return 0
        if args.command == "compile":
            target = args.output or args.file.with_suffix(args.file.suffix + ".ir.json")
            dump_json(ir, target)
            print(f"Compiled Model IR: {target}")
            return 0
        result = run_ir(ir)
        if args.output:
            dump_json(result, args.output)
            print(f"Run manifest: {args.output}")
        else:
            print(json.dumps(result, indent=2, sort_keys=True, ensure_ascii=False))
        return 0
    except (OSError, CompileError, ValueError) as exc:
        print(str(exc), file=sys.stderr)
        return 2


if __name__ == "__main__":
    raise SystemExit(main())
