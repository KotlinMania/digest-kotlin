# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 14/14 (100.0%)
- **Function parity:** 49/81 matched (target 77) — 60.5%
- **Class/type parity:** 31/41 matched (target 47) — 75.6%
- **Combined symbol parity:** 80/122 matched (target 124) — 65.6%
- **Average inline-code cosine:** 0.29 (function body across 14 matched files)
- **Average documentation cosine:** 0.61 (doc text across 14 matched files)
- **Cheat-zeroed Files:** 5
- **Critical Issues:** 12 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. core_api.wrapper

- **Target:** `coreapi.Wrapper`
- **Similarity:** 0.37
- **Dependents:** 0
- **Priority Score:** 132106.3
- **Functions:** 7/13 matched (target 10)
- **Missing functions:** `from_core`, `new`, `new_from_slice`, `fmt`, `write`, `flush`
- **Types:** 1/8 matched (target 2)
- **Missing types:** `BlockSize`, `KeySize`, `OutputSize`, `Reader`, `CoreProxy`, `Sealed`, `Core`

### 2. mac

- **Target:** `digest.Mac`
- **Similarity:** 0.38
- **Dependents:** 0
- **Priority Score:** 82306.2
- **Functions:** 11/19 matched (target 15)
- **Missing functions:** `new_from_slice`, `update`, `finalize`, `finalize_reset`, `reset`, `generate_key`, `eq`, `fmt`
- **Types:** 4/4 matched
- **Missing types:** _none_

### 3. digest

- **Target:** `digest.Digest`
- **Similarity:** 0.26
- **Dependents:** 0
- **Priority Score:** 61607.4
- **Functions:** 7/13 matched
- **Missing functions:** `update`, `finalize_into`, `finalize_into_reset`, `reset`, `box_clone`, `clone`
- **Types:** 3/3 matched (target 4)
- **Missing types:** _none_

### 4. core_api.ct_variable

- **Target:** `coreapi.CtVariable`
- **Similarity:** 0.39
- **Dependents:** 0
- **Priority Score:** 51006.1
- **Functions:** 3/5 matched (target 3)
- **Missing functions:** `default`, `write_alg_name`
- **Types:** 2/5 matched (target 2)
- **Missing types:** `BlockSize`, `OutputSize`, `BufferKind`

### 5. core_api.rt_variable

- **Target:** `coreapi.RtVariable`
- **Similarity:** 0.51
- **Dependents:** 0
- **Priority Score:** 31104.9
- **Functions:** 7/10 matched (target 7)
- **Missing functions:** `fmt`, `write`, `flush`
- **Types:** 1/1 matched
- **Missing types:** _none_

### 6. dev.fixed

- **Target:** `dev.Fixed [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 20210.0
- **Functions:** 0/2 matched (target 0)
- **Missing functions:** `fixed_reset_test`, `fixed_test`
- **Types:** 0/0 matched
- **Missing types:** _none_

### 7. dev.variable

- **Target:** `dev.Variable [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 20210.0
- **Functions:** 0/2 matched (target 0)
- **Missing functions:** `variable_reset_test`, `variable_test`
- **Types:** 0/0 matched
- **Missing types:** _none_

### 8. lib

- **Target:** `digest.Lib`
- **Similarity:** 0.72
- **Dependents:** 0
- **Priority Score:** 12102.8
- **Functions:** 10/11 matched (target 13)
- **Missing functions:** `fmt`
- **Types:** 10/10 matched (target 19)
- **Missing types:** _none_

### 9. core_api.xof_reader

- **Target:** `coreapi.XofReader`
- **Similarity:** 0.15
- **Dependents:** 0
- **Priority Score:** 10308.5
- **Functions:** 1/2 matched (target 1)
- **Missing functions:** `fmt`
- **Types:** 1/1 matched
- **Missing types:** _none_

### 10. dev.xof

- **Target:** `dev.Xof [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10110.0
- **Functions:** 0/1 matched (target 0)
- **Missing functions:** `xof_reset_test`
- **Types:** 0/0 matched
- **Missing types:** _none_

### 11. core_api

- **Target:** `coreapi.CoreApi [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 810.0
- **Functions:** 0/0 matched (target 10)
- **Missing functions:** _none_
- **Types:** 8/8 matched (target 13)
- **Missing types:** _none_

### 12. dev.rng

- **Target:** `dev.Rng`
- **Similarity:** 0.34
- **Dependents:** 0
- **Priority Score:** 306.6
- **Functions:** 2/2 matched
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 13. dev

- **Target:** `dev.Dev`
- **Similarity:** 0.91
- **Dependents:** 0
- **Priority Score:** 100.9
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 14. dev.mac

- **Target:** `dev.Mac [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched (target 2)
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

