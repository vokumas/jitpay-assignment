package io.vokumas.jitpayassignment.util;

public final class Pair<L, R> {

    private final L left;
    private final R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L left() {
        return this.left;
    }

    public R right() {
        return this.right;
    }

}
