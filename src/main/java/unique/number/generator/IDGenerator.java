package unique.number.generator;

public class IDGenerator {

    private long startingEpoch = 1288834974657L;

    private long sequence = 0L;
    private int workerIdBits = 5;
    private int datacenterIdBits = 5;
    private int sequenceBits = 12;

    private int workerIdShift = sequenceBits;
    private int datacenterIdShift = sequenceBits + workerIdBits;
    private int timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private long sequenceMask = -1L ^ (-1L << sequenceBits);

    private long lastTimestamp = -1L;

    private long workerId = 31;
    private long datacenterId = 31;

    public long getId() {
        return nextId();
    }

    private long nextId() {

        long timestamp = timeGen();

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }

        } else {
            sequence = 0;
        }


        if (timestamp < lastTimestamp) {
            throw new RuntimeException("This should not happen.");
        }


        lastTimestamp = timestamp;


        return ((timestamp - twepoch) << timestampLeftShift) |
                (datacenterId << datacenterIdShift) | // 5 Bytes
                (workerId << workerIdShift) | // 5 Byte
                sequence; // 12 Byte

    }

    private String convertToString(long v) {
        StringBuilder sb = new StringBuilder();

        while (v > 0) {
            int remainder = (int) (v % 62);
            sb.insert(0, getChar(remainder));
            v = v / 62;
        }

        return sb.toString();
    }

    private char getChar(int number) {
        if (number >= 0 && number < 10) {
            return (char) ('0' + number);
        } else if (number >= 10 && number < 36) {
            return (char) ('a' + (number - 10));
        } else {
            return (char) ('A' + (number - 36));
        }
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }


    private long timeGen() {
        return System.currentTimeMillis();
    }

}
