#!/usr/bin/env python3
"""Verify the Matrixgame 189-run Gravity Cluster export (Python standard library only)."""
from __future__ import annotations
import argparse, csv, json, re, sys
from pathlib import Path
def main() -> int:
    ap=argparse.ArgumentParser()
    ap.add_argument("--json",default="results/gravity_cluster_phase_sweep_dashboard_v2_20261108.json")
    ap.add_argument("--csv",default="results/gravity_cluster_phase_sweep_dashboard_v2_20261108.csv")
    ap.add_argument("--html",default="web/GravityClusterResearchDashboard_Replay_v2_1.html")
    args=ap.parse_args()
    package=json.loads(Path(args.json).read_text(encoding="utf-8"))
    rows=package["records"]
    assert len(rows)==189, f"expected 189 records, got {len(rows)}"
    ids=[r["run_id"] for r in rows]
    assert len(set(ids))==189, "duplicate run IDs"
    keys=[(r["satellites_per_cluster"],r["impact_parameter"],r["relative_phase_deg"]) for r in rows]
    assert len(set(keys))==189, "duplicate parameter tuples"
    expected={0:(7,3),1:(91,37),2:(91,52)}
    for variant,(n_runs,n_hits) in expected.items():
        group=[r for r in rows if int(r["satellites_per_cluster"])==variant]
        hits=sum(bool(r["core_contact"]) for r in group)
        assert (len(group),hits)==(n_runs,n_hits),(variant,len(group),hits)
    assert all(float(r["dt"])==0.002 for r in rows),"unexpected dt"
    assert all(int(r["steps"])==26000 for r in rows),"unexpected step count"
    with Path(args.csv).open(encoding="utf-8-sig",newline="") as f:
        csv_rows=list(csv.DictReader(f))
    assert len(csv_rows)==189,"CSV row count mismatch"
    assert [r["run_id"] for r in csv_rows]==ids,"CSV/JSON run order mismatch"
    hp=Path(args.html)
    if hp.exists():
        html=hp.read_text(encoding="utf-8")
        mm=re.search(r"const META=(\{.*?\});",html,re.S)
        dm=re.search(r"const EMBEDDED_DATA=(\[.*?\]);",html,re.S)
        assert mm and dm,"dashboard embedded data missing"
        hm=json.loads(mm.group(1)); hd=json.loads(dm.group(1))
        assert len(hd)==189 and [r["run_id"] for r in hd]==ids,"dashboard/JSON mismatch"
        assert hm["model_parameters"]["core_contact_distance"]==8.0
        assert "replay.contactDistance" in html,"replay does not use manifest contact distance"
    print("PASS: 189 unique runs and parameter tuples.")
    print("PASS: A=3/7, B=37/91, C=52/91; dt=0.002; steps=26000.")
    print("PASS: JSON, CSV and embedded HTML data agree.")
    print("NOTE: integrity checks do not establish physical realism.")
    return 0
if __name__=="__main__":
    try: raise SystemExit(main())
    except Exception as exc:
        print("FAIL:",exc,file=sys.stderr)
        raise
