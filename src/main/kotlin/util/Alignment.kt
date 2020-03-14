package util

import htsjdk.samtools.SAMRecord

fun pileUpStart(record: SAMRecord, forwardShift: Int = 0, reverseShift: Int = 0): Int {
    return if (!record.readNegativeStrandFlag) {
        (record.start + forwardShift)
    } else {
        (record.end + reverseShift)
    }
}
