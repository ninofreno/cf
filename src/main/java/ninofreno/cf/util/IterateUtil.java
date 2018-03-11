package ninofreno.cf.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public final class IterateUtil {

    private IterateUtil() {
        // static methods only
    }

    public static <T> Iterable<T> filter(final Iterable<T> iterable, Predicate<T> predicate) {

        return new Iterable<T>() {

            private T next;

            @Override
            public Iterator<T> iterator() {

                final Iterator<T> iterator = iterable.iterator();
                return new Iterator<T>() {

                    @Override
                    public boolean hasNext() {

                        if (next == null) {
                            while (iterator.hasNext()) {
                                next = iterator.next();
                                if (predicate.test(next)) {
                                    return true;
                                } else {
                                    next = null;
                                }
                            }
                        }
                        return next != null;
                    }

                    @Override
                    public T next() {

                        if (this.hasNext()) {
                            final T result = next;
                            next = null;
                            return result;
                        } else {
                            throw new NoSuchElementException();
                        }
                    }
                };
            }
        };
    }

}
