package karatests.controllers

import kara.controllers.*


class CrudController() : BaseController() {

    Get("") fun index() : ActionResult {
        return TextResult("index")
    }

    Get(":id") fun show(id : Int) : ActionResult {
        return TextResult("show $id")
    }

    Post("") fun create() : ActionResult {
        return TextResult("create")
    }

    Put(":id") fun update(id : Int) : ActionResult {
        return TextResult("update ${id}")
    }

    Delete(":id") fun delete() : ActionResult {
        return TextResult("delete ${params["id"]}")
    }

}