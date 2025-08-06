package com.mecoo.operation.scrapers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.compress.utils.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * @author: lin
 * @date: 2025-06-27 10:50
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrightDataTargetLink implements Serializable {

    private static final long serialVersionUID = 1L;

    private String url;


    public static BrightDataTargetLink buildTargetlink(String link) {
        return new BrightDataTargetLink(link);
    }

    public static List<BrightDataTargetLink> buildTargetlinks(List<String> links) {

        List<BrightDataTargetLink> Targetlinks = Lists.newArrayList();
        for (String link : links) {
            Targetlinks.add(new BrightDataTargetLink(link));
        }
        return Targetlinks;
    }

}
