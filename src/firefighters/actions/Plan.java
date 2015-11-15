package firefighters.actions;

import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Plan {
	@NonNull @Getter private List<AbstractAction> steps;
	
	public void executeNextStep() {
		steps.remove(0).execute();
	}
	
}
