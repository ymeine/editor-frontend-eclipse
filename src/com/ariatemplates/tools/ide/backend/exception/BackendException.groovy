package com.ariatemplates.tools.ide.backend.exception



class BackendException extends Exception {
	static buildMessage(content) {
		if (content instanceof Map) {
			def msg = content["msg"]
			content.remove "msg"
			"$msg: $content"
		} else {
			"$content"
		}
	}



	BackendException(content) {
		super(this.class.buildMessage content)
	}
}
