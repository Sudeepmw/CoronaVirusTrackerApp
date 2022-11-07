package com.skv.coronavirustracker.Controller;

import com.skv.coronavirustracker.Model.LocationStats;
import com.skv.coronavirustracker.Service.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
   private CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")
    public String homePage(Model model) {
        List<LocationStats> allStats = coronaVirusDataService.getAllStats();
        int toalCases = allStats.stream().mapToInt(i->i.getLatestTotalCases()).sum();
        int newCases = allStats.stream().mapToInt(i->i.getDiffFromPrevDay()).sum();
        model.addAttribute("totalNewCases",newCases);
        model.addAttribute("totalReportedCases",toalCases);
        model.addAttribute("locationStats", allStats);
        return "home";
    }
}
