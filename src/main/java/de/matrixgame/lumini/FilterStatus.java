package de.matrixgame.lumini;
/** Explicit outcomes for pipeline stages; NOT_RUN is never equivalent to PASS. */
public enum FilterStatus { CAPTURED, PASS, REVIEW_REQUIRED, NOT_RUN, INPUT_ERROR }
