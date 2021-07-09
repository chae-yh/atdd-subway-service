package nextstep.subway.line.domain;

import nextstep.subway.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;
    @Embedded
    private Sections sections = new Sections();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        sections.addLineStation(new Section(this, upStation, downStation, distance));
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

	public List<Station> getStations() {
		return sections.getStations();
	}

	public void addLineStation(Station upStation, Station downStation, int distance) {
		sections.addLineStation(new Section(this, upStation, downStation, distance));
	}

	public void removeLineStation(Station station) {
    	sections.removeLineStation(this, station);
	}

	public void setLengthBetweenTwoStation(WeightedMultigraph<String, DefaultWeightedEdge> graph) {
		sections.setLengthBetweenTwoStation(graph);
	}

	public void setStations(WeightedMultigraph<String, DefaultWeightedEdge> graph) {
		for (Station station : getStations()) {
			graph.addVertex(station.getName());
		}
	}

	public void setStationsGraph(WeightedMultigraph<String, DefaultWeightedEdge> graph) {
		setStations(graph);
		setLengthBetweenTwoStation(graph);
	}
}
