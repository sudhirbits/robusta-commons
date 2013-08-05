package com.robusta.commons.web.model;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;

public class FindResponse<View> extends Response {

    protected long count;
    protected List<View> viewModels;

    public FindResponse() {
        super();
        this.viewModels = newArrayList();
    }

    public long getCount() {
        return count;
    }

    public FindResponse<View> withCount(long count) {
        this.count = count;
        return this;
    }

    public List<View> getViewModels() {
        return viewModels;
    }

    public FindResponse<View> withViewModels(List<View> viewModels){
        this.viewModels.addAll(viewModels);
        return this;
    }

    public FindResponse<View> withViewModels(View... viewModels){
        checkArgument(viewModels != null);
        Collections.addAll(this.viewModels, viewModels);
        return this;
    }
}
