package bran;

public interface Cache<T> {

	default boolean created() {
		return access() != null;
	}
	T access();
	T create();

	default T get() {
		if (created())
			return access();
		else
			return create();
	}

}
