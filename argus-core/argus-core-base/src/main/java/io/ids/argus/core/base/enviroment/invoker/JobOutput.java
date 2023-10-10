package io.ids.argus.core.base.enviroment.invoker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class JobOutput implements IInvokeOutput {
    private String params;
    private String job;
    private String name;
}
