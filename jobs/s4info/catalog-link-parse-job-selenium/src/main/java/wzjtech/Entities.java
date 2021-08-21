package wzjtech;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Entities {

    @Getter
    @Setter
    @ToString
    public static class Catalog implements Serializable {
        private String name;
        private String url;
        private int pageCount;
        private int articleCount;
        private Temporal createTime;
        transient private List<String> pages = new ArrayList<>();

        public void generateSubPageUrls(Function<Integer, String> prefixSupplier) {
            var pageList = IntStream.rangeClosed(1, pageCount).boxed()
                    .map(prefixSupplier)
                    .collect(Collectors.toList());
            pages.addAll(pageList);
        }
    }

}
