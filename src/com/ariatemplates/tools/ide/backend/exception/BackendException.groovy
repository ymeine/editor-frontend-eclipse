package com.ariatemplates.tools.ide.backend.exception



class BackendException extends Exception {
	private static final long serialVersionUID = 1L



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
		super(this.class.buildMessage(content))
	}
}
