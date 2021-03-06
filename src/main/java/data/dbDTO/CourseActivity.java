package data.dbDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Reference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Christian on 11-05-2017.
 */
@Data
@NoArgsConstructor
public class CourseActivity extends BaseDTO {

    private String title;
    private String Description;
    private ActivityStatus status = ActivityStatus.VISIBLE;

    private Date startDate;
    private Date EndDate;
    @Reference
    private List<CourseActivityElement> activityElementList = new ArrayList<>();

    public enum ActivityStatus{
        INVISIBLE, DRAFT, VISIBLE
    }
}
