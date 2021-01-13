package vn.com.vndirect.report.pms.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Beneficiary {
    private Long id;
    private String role;
    private String title;
    private String jobDescription;
    private List<Pos> pos = new ArrayList<>();
    private Certificate certificate;
}
