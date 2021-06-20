package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
	private static final int MINIMUM_SECITON_SIZE = 1;
	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	public List<Station> getStations() {
		if (sections.isEmpty()) {
			return Arrays.asList();
		}

		List<Station> stations = new ArrayList<>();
		Station downStation = findUpStation();
		stations.add(downStation);

		while (downStation != null) {
			Station finalDownStation = downStation;
			Optional<Section> nextLineStation = sections.stream()
				.filter(it -> it.getUpStation().equals(finalDownStation))
				.findFirst();
			if (!nextLineStation.isPresent()) {
				break;
			}
			downStation = nextLineStation.get().getDownStation();
			stations.add(downStation);
		}

		return stations;
	}

	private Station findUpStation() {
		Station downStation = sections.get(0).getUpStation();
		while (downStation != null) {
			Station finalDownStation = downStation;
			Optional<Section> nextLineStation = sections.stream()
				.filter(it -> it.getDownStation().equals(finalDownStation))
				.findFirst();
			if (!nextLineStation.isPresent()) {
				break;
			}
			downStation = nextLineStation.get().getUpStation();
		}

		return downStation;
	}

	public void addLineStation(Section section) {
		if (sections.isEmpty()) {
			sections.add(section);
			return;
		}

		List<Station> stations = getStations();
		boolean isUpStationExisted = isUpStationExisted(section, stations);
		boolean isDownStationExisted = isDownStationExisted(section, stations);

		validateTwoStationsAlreadyExists(isUpStationExisted, isDownStationExisted);
		validateConnectedStationToOldLineExists(section, stations);

		if (isUpStationExisted) {
			modifyCommonUpStationSection(section);
			sections.add(section);

			return;
		}

		if (isDownStationExisted) {
			modifyCommonDownStationSection(section);
			sections.add(section);

			return;
		}

		throw new RuntimeException();
	}

	private void modifyCommonDownStationSection(Section section) {
		sections.stream()
			.filter(it -> it.getDownStation().equals(section.getDownStation()))
			.findFirst()
			.ifPresent(it -> it.updateDownStation(section.getUpStation(), section.getDistance()));
	}

	private void modifyCommonUpStationSection(Section section) {
		sections.stream()
			.filter(it -> it.getUpStation().equals(section.getUpStation()))
			.findFirst()
			.ifPresent(it -> it.updateUpStation(section.getDownStation(), section.getDistance()));
	}

	private boolean isDownStationExisted(Section section, List<Station> stations) {
		return stations.stream().anyMatch(it -> it.equals(section.getDownStation()));
	}

	private boolean isUpStationExisted(Section section, List<Station> stations) {
		return stations.stream().anyMatch(it -> it.equals(section.getUpStation()));
	}

	private void validateConnectedStationToOldLineExists(Section section, List<Station> stations) {
		if (!stations.isEmpty() && stations.stream().noneMatch(it -> it.equals(section.getUpStation())) &&
			stations.stream().noneMatch(it -> it.equals(section.getDownStation()))) {
			throw new RuntimeException("등록할 수 없는 구간 입니다.");
		}
	}

	private void validateTwoStationsAlreadyExists(boolean isUpStationExisted, boolean isDownStationExisted) {
		if (isUpStationExisted && isDownStationExisted) {
			throw new RuntimeException("이미 등록된 구간 입니다.");
		}
	}

	public void removeLineStation(Line line, Station station) {
		validateStationRemovableInSections();

		Optional<Section> upLineStation = findCommonUpStationSection(station);
		Optional<Section> downLineStation = findCommonDownStationSection(station);

		if (upLineStation.isPresent() && downLineStation.isPresent()) {
			connectTwoStation(line, upLineStation, downLineStation);
		}

		remove(upLineStation);
		remove(downLineStation);
	}

	private void remove(Optional<Section> optionalSection){
		optionalSection.ifPresent(it -> sections.remove(it));
	}

	private void connectTwoStation(Line line, Optional<Section> upLineStation, Optional<Section> downLineStation) {
		Station newUpStation = downLineStation.get().getUpStation();
		Station newDownStation = upLineStation.get().getDownStation();
		int newDistance = upLineStation.get().getDistance() + downLineStation.get().getDistance();

		sections.add(new Section(line, newUpStation, newDownStation, newDistance));
	}

	private Optional<Section> findCommonDownStationSection(Station station) {
		return sections.stream()
			.filter(it -> it.getDownStation().equals(station))
			.findFirst();
	}

	private Optional<Section> findCommonUpStationSection(Station station) {
		return sections.stream()
			.filter(it -> it.getUpStation().equals(station))
			.findFirst();
	}

	private void validateStationRemovableInSections() {
		if (sections.size() <= MINIMUM_SECITON_SIZE) {
			throw new RuntimeException();
		}
	}
}
