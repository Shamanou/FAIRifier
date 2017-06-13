var test_baseuri = new function() {
	console.log($("span:contains('" + window.location.protocol + "//" + window.location.host + "/')")[0]);
    test = newTest();
    action (test, 'click',        {link:  'RDF'});
    wait   (test, "forElement", { jquery: '("a.menu-item:contains(\'Edit Semantic Model...\')")'});
    action (test, 'click',        { link: 'Edit Semantic Model...'});
    assert (test, function(){
    	jum.assertNotUndefined($("span:contains('" + window.location.protocol + "//" + window.location.host + "/')")[0]);
    });
    this.test_default_base_uri = test;
};