import {useCallback, useState} from "react";
import axios from 'axios';

export const useCenteredTree = (defaultTranslate = {x: 0, y: 0}) => {
    const [translate, setTranslate] = useState(defaultTranslate);
    const containerRef = useCallback((containerElem) => {
        if (containerElem !== null) {
            const {width, height} = containerElem.getBoundingClientRect();
            setTranslate({x: width / 2, y: height / 5});
        }
    }, []);
    return [translate, containerRef];
};

let newestReferences;
let newestPossibleRoots;

export function translateData(treeResponse) {
    console.log(treeResponse)
    // let references = Object.values(treeResponse.references);
    let references = new Map();
    treeResponse.references.forEach(element => {
        references.set(element.id, element.value)
    });
    newestReferences = references;
    // let references = new Map(Object.entries(treeResponse.references));
    let relations = new Map(Object.entries(treeResponse.relations));
    let leafs = new Map(Object.entries(treeResponse.leafs));
    let modified = treeResponse.modified
    console.log("references: ", references)
    console.log("relations: ", relations)
    console.log("leafs: ", leafs)
    console.log("Modified: ", modified)

    let visualTreeElems = new Map();
    references.forEach((value, key, map) => {
        // console.log("In map foreach. Key: ", key, " Value: ", value)
        visualTreeElems.set(key.toString(), {name: value.toString(), attributes: {"id": key.toString(),}, children: []})
    })
    console.log("Visual tree elems: ", visualTreeElems)
    let childrenIds = [];   // list of all childrens ID. ID which is not here is a root

    relations.forEach((relationChildren, parentId, map) => {
        childrenIds.push(...relationChildren)
        console.log("relation children: ", relationChildren)
        relationChildren.forEach(childId => {
            let parent = visualTreeElems.get(parentId.toString())
            // console.log("Parent Id: ", parentId," Parent: ",parent)
            parent.children.push(visualTreeElems.get(childId.toString()))
        })
    })
    leafs.forEach((sum, lastNodeId, map) => {
            visualTreeElems.get(lastNodeId.toString()).children.push({name: sum.toString(), children: []})
        }
    )

    console.log("Visual tree: ", visualTreeElems)
    let possibleRoots = []
    visualTreeElems.forEach((v, key, map) => {
        if (!childrenIds.includes(parseInt(key))) {
            possibleRoots.push(key)
        }
    })
    console.log("Possible roots: ", possibleRoots)
    newestPossibleRoots = possibleRoots;
    console.log("returned data: ", visualTreeElems.get(possibleRoots.toString()))
    return {tree: visualTreeElems.get(possibleRoots.toString()), modified: treeResponse.modified};
}

const serverAdress = 'localhost'
const serverPort = 8080
export const getData = async (setData, setError, setLoading) => {
    callApi({method: 'GET'}, `http://${serverAdress}:${serverPort}/tree/getTree`, setData, setError, setLoading)
}

export const callApi = async (postParams, url, setData, setError, setLoading) => {
    fetch(url, postParams)
        .then((response) => {
            if (response.ok) {
                return response.json(); // Parse the response data as JSON
            } else {
                throw new Error('API request failed');
            }
        })
        .then((actualData) => {
            let temp = translateData(actualData)
            actualData = temp.tree
            console.log("Tree data: ", actualData)
            setData(actualData);
            setError(null);
        })
        .catch((err) => {
            setError(err.message);
            setData(null);
        })
        .finally(() => {
            setLoading(false);
            console.log("Loading set to false")
        });
}

export const callDeleteNode = async (nodeId, setData, setError, setLoading) => {
    callApi({method: 'DELETE'}, `http://${serverAdress}:${serverPort}/tree/deleteNode/${nodeId}`, setData, setError, setLoading)
}

export const callAddNode = (newNode, setResponse) => {
    if (newNode.parentId == null) {
        console.log("Sent node: ", newNode)
        fetch(`http://${serverAdress}:${serverPort}/tree/addNode`, {
            method: 'POST', headers: {
                "Content-Type": "application/json; charset=utf-8"
            }, body: JSON.stringify(newNode)
        })
            .then((res) => res.json())
            .then((json) => setResponse(json))
            .catch((err) => console.log("Send node error: ", err))
    }


    // callApi({method:'POST'},`http://${serverAdress}:${serverPort}/tree/addNode` ,setData, setError, setLoading)
}

export const setNewRoot = (currentTree, newRoot, setTreeData) => {
    const oldRootModification = {
        nodeId: currentTree.attributes.id,
        value: parseInt(currentTree.name),
        parentId: newRoot.id
    }
    fetch(`http://${serverAdress}:${serverPort}/tree/editNode`, {
        method: 'PUT', headers: {
            "Content-Type": "application/json; charset=utf-8"
        }, body: JSON.stringify(oldRootModification)
    })
        .then((res) => res.json())
        .then((json) => {
                let res = translateData(json)
                setTreeData(res.tree)
            console.log("Updated tree data set: ", res)
            }
        )
        .catch((err) => console.log("Send node error: ", err))
}