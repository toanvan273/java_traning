package vn.com.vndirect.report.datasource.mapdb;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public interface DbStorageRepository<T extends java.io.Serializable> {
	T add(@NotNull String paramString1, @NotNull String paramString2, @NotNull T paramT) throws Exception;

	T update(@NotNull String paramString1, @NotNull String paramString2, @NotNull T paramT) throws Exception;

	boolean delete(@NotNull String paramString1, @NotNull String paramString2) throws Exception;

	boolean delete(@NotNull String paramString, @NotEmpty List<String> paramList) throws Exception;

	boolean creatOrReplace() throws Exception;

	boolean contains(@NotNull String paramString1, @NotNull String paramString2) throws Exception;

	Optional<T> findOne(@NotNull String paramString1, @NotNull String paramString2) throws Exception;

	Map<String, T> findAll(@NotNull String paramString) throws Exception;

	long countAll(@NotNull String paramString) throws Exception;
}