package com.itmo.utility.abstracted.interfaces;

import com.itmo.utility.ExecutionResponse;

public interface Executable {
    ExecutionResponse apply(String[] arguments);
}
